package com.fitassist.backend.service.implementation.user.interaction.withType;

import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotSupportedInteractionTypeException;
import com.fitassist.backend.mapper.CommentMapper;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.user.TypeOfInteraction;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserComment;
import com.fitassist.backend.repository.UserCommentRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.comment.CommentPopulationService;
import com.fitassist.backend.service.declaration.user.SavedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userCommentService")
public class UserCommentServiceImpl extends GenericSavedService<Comment, UserComment, CommentResponseDto>
		implements SavedService {

	private final CommentMapper commentMapper;

	private final CommentPopulationService commentPopulationService;

	public UserCommentServiceImpl(UserRepository userRepository, JpaRepository<Comment, Integer> entityRepository,
			JpaRepository<UserComment, Integer> userEntityRepository, CommentMapper mapper,
			CommentPopulationService commentPopulationService) {
		super(userRepository, entityRepository, userEntityRepository, Comment.class, UserComment.class);
		this.commentMapper = mapper;
		this.commentPopulationService = commentPopulationService;
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
		Page<UserComment> userCommentPage = ((UserCommentRepository) userEntityRepository)
			.findAllByUserIdAndType(userId, type, pageable);

		List<CommentSummaryDto> summaries = userCommentPage.getContent().stream().map(uc -> {
			CommentSummaryDto dto = commentMapper.toSummaryDto(uc.getComment());
			dto.setInteractionCreatedAt(uc.getCreatedAt());
			return dto;
		}).toList();

		commentPopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userCommentPage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type) {
		return ((UserCommentRepository) userEntityRepository).existsByUserIdAndCommentIdAndType(userId, entityId, type);
	}

	@Override
	protected UserComment createUserEntity(User user, Comment entity, TypeOfInteraction type) {
		if (type == TypeOfInteraction.SAVE) {
			throw new NotSupportedInteractionTypeException("Cannot save a comment as a saved entity.");
		}
		return UserComment.of(user, entity, type);
	}

	@Override
	protected Optional<UserComment> findUserEntityOptional(int userId, int entityId, TypeOfInteraction type) {
		return ((UserCommentRepository) userEntityRepository).findByUserIdAndCommentIdAndType(userId, entityId, type);
	}

}

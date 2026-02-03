package com.fitassist.backend.service.implementation.user.interaction.withoutType;

import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.forumThread.ForumThreadMapper;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserThread;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.repository.UserThreadRepository;
import com.fitassist.backend.service.declaration.thread.ForumThreadPopulationService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userThreadService")
public class UserThreadServiceImpl
		extends GenericSavedServiceWithoutType<ForumThread, UserThread, ForumThreadSummaryDto>
		implements SavedServiceWithoutType {

	private final ForumThreadMapper forumThreadMapper;

	private final ForumThreadPopulationService forumThreadPopulationService;

	public UserThreadServiceImpl(UserThreadRepository userThreadRepository, ForumThreadRepository forumThreadRepository,
			UserRepository userRepository, ForumThreadMapper forumThreadMapper,
			ForumThreadPopulationService forumThreadPopulationService) {
		super(userRepository, forumThreadRepository, userThreadRepository, forumThreadMapper::toSummaryDto,
				ForumThread.class);
		this.forumThreadMapper = forumThreadMapper;
		this.forumThreadPopulationService = forumThreadPopulationService;
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, Pageable pageable) {
		Page<UserThread> userThreadPage = ((UserThreadRepository) userEntityRepository).findAllByUserId(userId,
				pageable);

		List<ForumThreadSummaryDto> summaries = userThreadPage.getContent().stream().map(ut -> {
			ForumThreadSummaryDto dto = forumThreadMapper.toSummaryDto(ut.getForumThread());
			dto.setInteractionCreatedAt(ut.getCreatedAt());
			return dto;
		}).toList();

		forumThreadPopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userThreadPage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int entityId) {
		return ((UserThreadRepository) userEntityRepository).existsByUserIdAndForumThreadId(userId, entityId);
	}

	@Override
	protected UserThread createUserEntity(User user, ForumThread entity) {
		return UserThread.of(user, entity);
	}

	@Override
	protected UserThread findUserEntity(int userId, int entityId) {
		return ((UserThreadRepository) userEntityRepository).findByUserIdAndForumThreadId(userId, entityId)
			.orElseThrow(() -> RecordNotFoundException.of(UserThread.class, userId, entityId));
	}

	@Override
	protected long countByEntityId(int entityId) {
		return ((UserThreadRepository) userEntityRepository).countByForumThreadId(entityId);
	}

}

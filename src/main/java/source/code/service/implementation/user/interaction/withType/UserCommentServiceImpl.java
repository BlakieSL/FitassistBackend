package source.code.service.implementation.user.interaction.withType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.CommentMapper;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserComment;
import source.code.repository.UserCommentRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.comment.CommentPopulationService;
import source.code.service.declaration.user.SavedService;

import java.util.List;
import java.util.Optional;

@Service("userCommentService")
public class UserCommentServiceImpl
        extends GenericSavedService<Comment, UserComment, CommentResponseDto>
        implements SavedService {

    private final CommentMapper commentMapper;
    private final CommentPopulationService commentPopulationService;

    public UserCommentServiceImpl(UserRepository userRepository,
                                  JpaRepository<Comment, Integer> entityRepository,
                                  JpaRepository<UserComment, Integer> userEntityRepository,
                                  CommentMapper mapper,
                                  CommentPopulationService commentPopulationService) {
        super(userRepository, entityRepository, userEntityRepository, mapper::toResponseDto, Comment.class, UserComment.class);
        this.commentMapper = mapper;
        this.commentPopulationService = commentPopulationService;
    }

    @Override
    public Page<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
        Page<UserComment> userCommentPage = ((UserCommentRepository) userEntityRepository)
                .findAllByUserIdAndType(userId, type, pageable);

        List<CommentSummaryDto> summaries = userCommentPage.getContent().stream()
                .map(uc -> {
                    CommentSummaryDto dto = commentMapper.toSummaryDto(uc.getComment());
                    dto.setInteractionCreatedAt(uc.getCreatedAt());
                    return dto;
                })
                .toList();

        commentPopulationService.populate(summaries);

        return new PageImpl<>(
                summaries.stream().map(dto -> (BaseUserEntity) dto).toList(),
                pageable,
                userCommentPage.getTotalElements()
        );
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type) {
        return ((UserCommentRepository) userEntityRepository)
                .existsByUserIdAndCommentIdAndType(userId, entityId, type);
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
        return ((UserCommentRepository) userEntityRepository)
                .findByUserIdAndCommentIdAndType(userId, entityId, type);
    }
}

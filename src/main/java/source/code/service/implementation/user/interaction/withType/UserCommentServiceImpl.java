package source.code.service.implementation.user.interaction.withType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.comment.CommentMapper;
import source.code.model.thread.Comment;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserComment;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedService;

import java.util.List;

@Service("userCommentService")
public class UserCommentServiceImpl
        extends GenericSavedService<Comment, UserComment, CommentResponseDto>
        implements SavedService {

    private final ImageUrlPopulationService imagePopulationService;

    public UserCommentServiceImpl(UserRepository userRepository,
                                  JpaRepository<Comment, Integer> entityRepository,
                                  JpaRepository<UserComment, Integer> userEntityRepository,
                                  CommentMapper mapper,
                                  ImageUrlPopulationService imagePopulationService) {
        super(userRepository, entityRepository, userEntityRepository, mapper::toResponseDto, Comment.class);
        this.imagePopulationService = imagePopulationService;
    }

    @Override
    public Page<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
        return ((CommentRepository) entityRepository)
                .findCommentSummaryUnified(userId, type, true, pageable)
                .map(dto -> {
                    imagePopulationService.populateAuthorImage(dto, CommentSummaryDto::getAuthorImageName,
                            CommentSummaryDto::setAuthorImageUrl);
                    return dto;
                });
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
    protected UserComment findUserEntity(int userId, int entityId, TypeOfInteraction type) {
        return ((UserCommentRepository) userEntityRepository)
                .findByUserIdAndCommentIdAndType(userId, entityId, type)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserComment.class,
                        userId,
                        entityId,
                        type
                ));
    }

    @Override
    protected List<UserComment> findAllByUserAndType(int userId, TypeOfInteraction type) {
        return ((UserCommentRepository) userEntityRepository)
                .findByUserIdAndType(userId, type);
    }

    @Override
    protected Comment extractEntity(UserComment userEntity) {
        return userEntity.getComment();
    }

    @Override
    protected long countSaves(int entityId) {
        return 0;
    }

    @Override
    protected long countLikes(int entityId) {
        long likes = ((UserCommentRepository) userEntityRepository)
                .countByCommentIdAndType(entityId, TypeOfInteraction.LIKE);
        long dislikes = ((UserCommentRepository) userEntityRepository)
                .countByCommentIdAndType(entityId, TypeOfInteraction.DISLIKE);

        return likes - dislikes;
    }
}

package source.code.service.implementation.user.interaction;

import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.comment.CommentMapper;
import source.code.model.forum.Comment;
import source.code.model.user.profile.User;
import source.code.model.user.UserCommentLikes;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentLikesRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userCommentService")
public class UserCommentServiceImpl
        extends GenericSavedServiceWithoutType<Comment, UserCommentLikes, CommentResponseDto>
        implements SavedServiceWithoutType {

    public UserCommentServiceImpl(UserCommentLikesRepository userCommentLikesRepository,
                                  CommentRepository commentRepository,
                                  UserRepository userRepository,
                                  CommentMapper commentMapper) {
        super(userRepository,
                commentRepository,
                userCommentLikesRepository,
                commentMapper::toResponseDto,
                Comment.class);
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId) {
        return ((UserCommentLikesRepository) userEntityRepository)
                .existsByUserIdAndCommentId(userId, entityId);
    }

    @Override
    protected UserCommentLikes createUserEntity(User user, Comment entity) {
        return UserCommentLikes.of(user, entity);
    }

    @Override
    protected UserCommentLikes findUserEntity(int userId, int entityId) {
        return ((UserCommentLikesRepository) userEntityRepository)
                .findByUserIdAndCommentId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserCommentLikes.class,
                        userId,
                        entityId
                ));
    }

    @Override
    protected List<UserCommentLikes> findAllByUser(int userId) {
        return ((UserCommentLikesRepository) userEntityRepository).findAllByUserId(userId);
    }

    @Override
    protected Comment extractEntity(UserCommentLikes userCommentLikes) {
        return userCommentLikes.getComment();
    }

    @Override
    protected long countSaves(int entityId) {
        return 0;
    }

    @Override
    protected long countLikes(int entityId) {
        return ((UserCommentLikesRepository) userEntityRepository)
                .countAllByCommentId(entityId);
    }
}

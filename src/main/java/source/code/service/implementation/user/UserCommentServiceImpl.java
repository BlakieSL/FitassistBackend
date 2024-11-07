package source.code.service.implementation.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.comment.CommentMapper;
import source.code.model.forum.Comment;
import source.code.model.user.User;
import source.code.model.user.UserCommentLikes;
import source.code.repository.CommentRepository;
import source.code.repository.UserCommentLikesRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.user.SavedService;

import java.util.List;
import java.util.function.Function;

@Service("userCommentService")
public class UserCommentServiceImpl
        extends GenericSavedService<Comment, UserCommentLikes, CommentResponseDto>
        implements SavedService {

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
    protected boolean isAlreadySaved(int userId, int entityId, short type) {
        return ((UserCommentLikesRepository) userEntityRepository)
                .existsByUserAndComment(userId, entityId);
    }

    @Override
    protected UserCommentLikes createUserEntity(User user, Comment entity, short type) {
        return UserCommentLikes.of(user, entity);
    }

    @Override
    protected UserCommentLikes findUserEntity(int userId, int entityId, short type) {
        return ((UserCommentLikesRepository) userEntityRepository)
                .findByUserIdAndCommentId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserCommentLikes.class,
                        userId,
                        entityId
                ));
    }

    @Override
    protected List<UserCommentLikes> findAllByUserAndType(int userId, short type) {
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

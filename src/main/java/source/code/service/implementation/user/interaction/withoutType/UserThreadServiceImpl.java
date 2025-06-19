package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.stereotype.Service;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.model.forum.ForumThread;
import source.code.model.user.User;
import source.code.model.user.UserThreadSubscription;
import source.code.repository.ForumThreadRepository;
import source.code.repository.UserRepository;
import source.code.repository.UserThreadSubscriptionRepository;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;
@Service("userThreadService")
public class UserThreadServiceImpl
        extends GenericSavedServiceWithoutType<ForumThread, UserThreadSubscription, ForumThreadResponseDto>
        implements SavedServiceWithoutType {

    public UserThreadServiceImpl(UserThreadSubscriptionRepository userThreadSubscriptionRepository,
                                 ForumThreadRepository forumThreadRepository,
                                 UserRepository userRepository,
                                 ForumThreadMapper forumThreadMapper) {
        super(userRepository,
                forumThreadRepository,
                userThreadSubscriptionRepository,
                forumThreadMapper::toResponseDto,
                ForumThread.class);
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId) {
        return ((UserThreadSubscriptionRepository) userEntityRepository)
                .existsByUserIdAndForumThreadId(userId, entityId);
    }

    @Override
    protected UserThreadSubscription createUserEntity(User user, ForumThread entity) {
        return UserThreadSubscription.of(user, entity);
    }

    @Override
    protected UserThreadSubscription findUserEntity(int userId, int entityId) {
        return ((UserThreadSubscriptionRepository) userEntityRepository)
                .findByUserIdAndForumThreadId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserThreadSubscription.class,
                        userId,
                        entityId
                ));
    }

    @Override
    protected List<UserThreadSubscription> findAllByUser(int userId) {
        return ((UserThreadSubscriptionRepository) userEntityRepository).findAllByUserId(userId);
    }

    @Override
    protected ForumThread extractEntity(UserThreadSubscription userEntity) {
        return userEntity.getForumThread();
    }

    @Override
    protected long countSaves(int entityId) {
        return 0;
    }

    @Override
    protected long countLikes(int entityId) {
        return 0;
    }
}

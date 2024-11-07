package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserThreadSubscription;

import java.util.List;
import java.util.Optional;

public interface UserThreadSubscriptionRepository
        extends JpaRepository<UserThreadSubscription, Integer>
{
    boolean existsByUserIdAndForumThreadId(int userId, int forumThreadId);
    Optional<UserThreadSubscription> findByUserIdAndForumThreadId(int userId, int forumThreadId);
    List<UserThreadSubscription> findAllByUserId(int userId);
}

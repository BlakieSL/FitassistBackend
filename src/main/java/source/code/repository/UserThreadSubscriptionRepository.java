package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserThreadSubscription;

public interface UserThreadSubscriptionRepository
        extends JpaRepository<UserThreadSubscription, Integer> {
}

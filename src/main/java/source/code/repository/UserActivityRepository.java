package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.User.UserActivity;

import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    boolean existsByUserIdAndActivityIdAndType(int userId, int activityId, short type);

    Optional<UserActivity> findByUserIdAndActivityIdAndType(int userId, int activityId, short type);

    List<UserActivity> findByUserId(int userId);

    List<UserActivity> findByUserIdAndType(int userId, short type);

    long countByActivityIdAndType(int activityId, short type);
}
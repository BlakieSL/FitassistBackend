package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserActivity;

import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    boolean existsByUserIdAndActivityId(int userId, int activityId);

    Optional<UserActivity> findByUserIdAndActivityId(int userId, int activityId);

    List<UserActivity> findAllByUserId(int userId);

    long countByActivityId(int activityId);
}
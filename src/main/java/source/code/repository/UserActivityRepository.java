package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.user.UserActivity;

import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    boolean existsByUserIdAndActivityId(int userId, int activityId);

    Optional<UserActivity> findByUserIdAndActivityId(int userId, int activityId);

    @Query("""
           SELECT ua FROM UserActivity ua
           JOIN FETCH ua.user
           JOIN FETCH ua.activity a
           JOIN FETCH a.activityCategory
           WHERE ua.user.id = :userId
           """)
    List<UserActivity> findAllByUserId(@Param("userId") int userId);

    long countByActivityId(int activityId);
}
package source.code.repository;

import org.springframework.data.domain.Sort;
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
           JOIN FETCH ua.activity a
           JOIN FETCH a.activityCategory
           LEFT JOIN FETCH a.mediaList
           WHERE ua.user.id = :userId
           """)
    List<UserActivity> findAllByUserIdWithMedia(@Param("userId") int userId, Sort sort);

    long countByActivityId(int activityId);
}
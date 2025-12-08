package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.model.user.UserActivity;

import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    boolean existsByUserIdAndActivityId(int userId, int activityId);

    long countByActivityId(int activityId);

    Optional<UserActivity> findByUserIdAndActivityId(int userId, int activityId);

    @Query(value = """
           SELECT ua FROM UserActivity ua
           JOIN FETCH ua.activity a
           JOIN FETCH a.activityCategory
           LEFT JOIN FETCH a.mediaList
           WHERE ua.user.id = :userId
           """)
    Page<UserActivity> findAllByUserIdWithMedia(@Param("userId") int userId, Pageable pageable);

    @Query("""
        SELECT COUNT(ua) as savesCount,
               SUM(CASE WHEN ua.user.id = :userId THEN 1 ELSE 0 END) as userSaved
        FROM UserActivity ua
        WHERE ua.activity.id = :activityId
    """)
    SavesProjection findSavesCountAndUserSaved(@Param("activityId") int activityId, @Param("userId") int userId);
}
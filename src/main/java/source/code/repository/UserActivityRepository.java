package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.activity.ActivityResponseDto;
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

    @Query("""
           SELECT new source.code.dto.response.activity.ActivityResponseDto(
               a.id,
               a.name,
               a.met,
               ac.name,
               ac.id,
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = a.id
                AND m.parentType = 'ACTIVITY'
                ORDER BY m.id ASC
                LIMIT 1),
               null,
               ua.createdAt)
           FROM UserActivity ua
           JOIN ua.activity a
           JOIN a.activityCategory ac
           WHERE ua.user.id = :userId
           ORDER BY ua.createdAt DESC
           """)
    List<ActivityResponseDto> findActivityDtosByUserId(@Param("userId") int userId);

    long countByActivityId(int activityId);
}
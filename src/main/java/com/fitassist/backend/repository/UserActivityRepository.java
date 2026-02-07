package com.fitassist.backend.repository;

import com.fitassist.backend.dto.pojo.projection.SavesProjection;
import com.fitassist.backend.model.user.interactions.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {

	boolean existsByUserIdAndActivityId(int userId, int activityId);

	Optional<UserActivity> findByUserIdAndActivityId(int userId, int activityId);

	long countByActivityId(int activityId);

	@Query(value = """
			SELECT ua FROM UserActivity ua
			JOIN FETCH ua.activity a
			JOIN FETCH a.activityCategory
			LEFT JOIN FETCH a.mediaList
			WHERE ua.user.id = :userId

			""")
	Page<UserActivity> findAllByUserIdWithMedia(@Param("userId") int userId, Pageable pageable);

	@Query("""
			    SELECT
			        ua.activity.id as entityId,
			        COUNT(ua) as savesCount,
			        SUM(CASE WHEN ua.user.id = :userId THEN 1 ELSE 0 END) as userSaved
			    FROM UserActivity ua
			    WHERE ua.activity.id = :activityId
			""")
	SavesProjection findSavesCountAndUserSaved(@Param("activityId") int activityId, @Param("userId") int userId);

	@Query("""
			    SELECT
			        ua.activity.id as entityId,
			        COUNT(ua) as savesCount,
			        SUM(CASE WHEN ua.user.id = :userId THEN 1 ELSE 0 END) as userSaved
			    FROM UserActivity ua
			    WHERE ua.activity.id IN :activityIds
			    GROUP BY ua.activity.id
			""")
	List<SavesProjection> findCountsAndInteractionsByActivityIds(@Param("userId") int userId,
			@Param("activityIds") List<Integer> activityIds);

}

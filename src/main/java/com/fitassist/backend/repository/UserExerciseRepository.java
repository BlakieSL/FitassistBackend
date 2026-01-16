package com.fitassist.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fitassist.backend.dto.pojo.projection.SavesProjection;
import com.fitassist.backend.model.user.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {

	boolean existsByUserIdAndExerciseId(int userId, int exerciseId);

	Optional<UserExercise> findByUserIdAndExerciseId(int userId, int exerciseId);

	@Query(value = """
			SELECT ue FROM UserExercise ue
			JOIN FETCH ue.exercise e
			JOIN FETCH e.expertiseLevel
			JOIN FETCH e.equipment
			JOIN FETCH e.mechanicsType
			JOIN FETCH e.forceType
			LEFT JOIN FETCH e.mediaList
			WHERE ue.user.id = :userId
			""")
	Page<UserExercise> findAllByUserIdWithMedia(@Param("userId") int userId, Pageable pageable);

	@Query("""
			    SELECT COUNT(ue) as savesCount,
			           SUM(CASE WHEN ue.user.id = :userId THEN 1 ELSE 0 END) as userSaved
			    FROM UserExercise ue
			    WHERE ue.exercise.id = :exerciseId
			""")
	SavesProjection findCountsAndInteractions(@Param("exerciseId") int exerciseId, @Param("userId") int userId);

	@Query("""
			    SELECT ue.exercise.id as entityId,
			           COUNT(ue) as savesCount,
			           SUM(CASE WHEN ue.user.id = :userId THEN 1 ELSE 0 END) as userSaved
			    FROM UserExercise ue
			    WHERE ue.exercise.id IN :exerciseIds
			    GROUP BY ue.exercise.id
			""")
	List<SavesProjection> findCountsAndInteractionsByExerciseIds(@Param("userId") int userId,
			@Param("exerciseIds") List<Integer> exerciseIds);

}

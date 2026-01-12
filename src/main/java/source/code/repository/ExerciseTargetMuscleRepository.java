package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.exercise.ExerciseTargetMuscle;

import java.util.List;

public interface ExerciseTargetMuscleRepository extends JpaRepository<ExerciseTargetMuscle, Integer> {

	@Query("""
			    SELECT etm
			    FROM ExerciseTargetMuscle etm
			    JOIN FETCH etm.exercise e
			    JOIN FETCH e.equipment
			    JOIN FETCH e.expertiseLevel
			    JOIN FETCH e.forceType
			    JOIN FETCH e.mechanicsType
			    WHERE etm.targetMuscle.id = :targetMuscleId
			""")
	List<ExerciseTargetMuscle> findByTargetMuscleId(@Param("targetMuscleId") int id);

}

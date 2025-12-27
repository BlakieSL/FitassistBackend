package source.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.text.ExerciseTip;

public interface ExerciseTipRepository extends JpaRepository<ExerciseTip, Integer> {

	List<ExerciseTip> getAllByExerciseId(int exerciseId);

}

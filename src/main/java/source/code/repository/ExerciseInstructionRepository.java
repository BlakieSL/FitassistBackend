package source.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.text.ExerciseInstruction;

public interface ExerciseInstructionRepository extends JpaRepository<ExerciseInstruction, Integer> {

	List<ExerciseInstruction> getAllByExerciseId(int exerciseId);

}

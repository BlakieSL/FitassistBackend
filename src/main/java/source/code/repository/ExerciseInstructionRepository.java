package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Exercise.ExerciseInstruction;

import java.util.List;

public interface ExerciseInstructionRepository extends JpaRepository<ExerciseInstruction, Integer> {
  List<ExerciseInstruction> getAllByExerciseId(int exerciseId);
}
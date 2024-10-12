package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Exercise.ExerciseType;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {
}
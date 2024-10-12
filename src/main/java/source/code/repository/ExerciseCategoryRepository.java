package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Exercise.ExerciseCategory;

import java.util.Optional;

public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Integer> {
  Optional<ExerciseCategory> findByName(String name);
}
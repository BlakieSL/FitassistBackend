package source.code.repository;

import source.code.model.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Integer> {
    Optional<ExerciseCategory> findByName(String name);
}
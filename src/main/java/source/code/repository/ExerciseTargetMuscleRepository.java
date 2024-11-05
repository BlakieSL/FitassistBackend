package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.exercise.ExerciseTargetMuscle;

import java.util.List;

public interface ExerciseTargetMuscleRepository extends JpaRepository<ExerciseTargetMuscle, Integer> {
    List<ExerciseTargetMuscle> findByTargetMuscleId(int id);
}
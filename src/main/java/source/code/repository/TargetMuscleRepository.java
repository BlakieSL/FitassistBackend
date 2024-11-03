package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Exercise.TargetMuscle;

public interface TargetMuscleRepository extends JpaRepository<TargetMuscle, Integer> {
}
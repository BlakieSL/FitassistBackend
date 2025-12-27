package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.exercise.TargetMuscle;

public interface TargetMuscleRepository extends JpaRepository<TargetMuscle, Integer> {

}

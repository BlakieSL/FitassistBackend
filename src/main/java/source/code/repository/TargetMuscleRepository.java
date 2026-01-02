package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.exercise.TargetMuscle;

import java.util.Collection;
import java.util.List;

public interface TargetMuscleRepository extends JpaRepository<TargetMuscle, Integer> {

	List<TargetMuscle> findAllByIdIn(List<Integer> ids);

}

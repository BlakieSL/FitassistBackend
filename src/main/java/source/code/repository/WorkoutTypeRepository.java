package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.WorkoutType;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Integer> {
}
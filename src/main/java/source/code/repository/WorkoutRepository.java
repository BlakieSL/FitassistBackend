package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Workout;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
}
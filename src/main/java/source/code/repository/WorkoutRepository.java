package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Workout.Workout;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Integer> {
    List<Workout> findAllByPlanId(int planId);
}
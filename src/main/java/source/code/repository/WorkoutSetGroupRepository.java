package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.workout.WorkoutSetGroup;

import java.util.List;
import java.util.Optional;

public interface WorkoutSetGroupRepository extends JpaRepository<WorkoutSetGroup, Integer> {
    @Query("""
        SELECT wsg
        FROM WorkoutSetGroup wsg
        LEFT JOIN FETCH wsg.workoutSets ws
        LEFT JOIN FETCH ws.exercise e
        WHERE wsg.id = :id
    """)
    List<WorkoutSetGroup> findAllByWorkoutId(Integer workoutId);

    @Query("""
        SELECT wsg
        FROM WorkoutSetGroup wsg
        LEFT JOIN FETCH wsg.workoutSets ws
        LEFT JOIN FETCH ws.exercise e
        WHERE wsg.id = :id
    """)
    Optional<WorkoutSetGroup> findByIdWithDetails(int id);
}

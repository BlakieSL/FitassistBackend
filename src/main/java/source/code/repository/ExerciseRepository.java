package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.exercise.Exercise;

import java.util.List;

public interface ExerciseRepository
        extends JpaRepository<Exercise, Integer>, JpaSpecificationExecutor<Exercise> {
    @EntityGraph(value = "Exercise.withoutAssociations")
    @Query("SELECT e FROM Exercise e")
    List<Exercise> findAllWithoutAssociations();
}
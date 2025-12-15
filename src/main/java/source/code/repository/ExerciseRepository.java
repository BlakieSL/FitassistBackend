package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.exercise.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer>, JpaSpecificationExecutor<Exercise> {
    @EntityGraph(value = "Exercise.summary")
    @NotNull
    @Override
    Page<Exercise> findAll(Specification<Exercise> spec, @NotNull Pageable pageable);

    @EntityGraph(value = "Exercise.withoutAssociations")
    @Query("SELECT e FROM Exercise e")
    List<Exercise> findAllWithoutAssociations();

    @Query("""
                SELECT e FROM Exercise e
                LEFT JOIN FETCH e.expertiseLevel
                LEFT JOIN FETCH e.equipment
                LEFT JOIN FETCH e.mechanicsType
                LEFT JOIN FETCH e.forceType
                LEFT JOIN FETCH e.exerciseTargetMuscles etm
                LEFT JOIN FETCH etm.targetMuscle
                LEFT JOIN FETCH e.exerciseInstructions
                LEFT JOIN FETCH e.exerciseTips
                WHERE e.id = :id
            """)
    Optional<Exercise> findByIdWithDetails(int id);
}
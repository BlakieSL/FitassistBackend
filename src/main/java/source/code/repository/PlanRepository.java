package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer>, JpaSpecificationExecutor<Plan> {

    @Override
    @EntityGraph(value = "Plan.summary")
    @NotNull
    Page<Plan> findAll(Specification<Plan> spec, @NotNull Pageable pageable);

    @Modifying
    @Query("UPDATE Plan p SET p.views = p.views + 1 WHERE p.id = :planId")
    void incrementViews(@Param("planId") Integer planId);

    @EntityGraph(value = "Plan.withoutAssociations")
    @Query("SELECT p FROM Plan p WHERE p.isPublic = true")
    List<Plan> findAllWithoutAssociations();

    @EntityGraph(value = "Plan.summary")
    @Query("""
        SELECT p
        FROM Plan p
        WHERE (:isPrivate IS NULL AND p.isPublic = true)
           OR (:isPrivate = false AND p.isPublic = true)
           OR (:isPrivate = true AND p.user.id = :userId)
    """)
    Page<Plan> findAllWithAssociations(
            @Param("isPrivate") Boolean isPrivate,
            @Param("userId") int userId,
            Pageable pageable
    );

    @EntityGraph(value = "Plan.summary")
    @Query("""
        SELECT p
        FROM Plan p
        JOIN p.workouts w
        JOIN w.workoutSetGroups wsg
        JOIN wsg.workoutSets ws
        WHERE ws.exercise.id = :exerciseId
          AND p.isPublic = true
        ORDER BY p.createdAt DESC
    """)
    List<Plan> findByExerciseIdWithDetails(@Param("exerciseId") int exerciseId);

    @EntityGraph(value = "Plan.summary")
    @Query("""
        SELECT p
        FROM Plan p
        WHERE ((:isOwnProfile = false OR :isOwnProfile IS NULL)
               AND p.isPublic = true
               AND p.user.id = :userId)
           OR (:isOwnProfile = true AND p.user.id = :userId)
    """)
    Page<Plan> findCreatedByUserWithDetails(
            @Param("userId") int userId,
            @Param("isOwnProfile") boolean isOwnProfile,
            Pageable pageable
    );

    @EntityGraph(value = "Plan.summary")
    @Query("""
        SELECT p
        FROM Plan p
        WHERE p.id IN :planIds
    """)
    List<Plan> findByIdsWithDetails(@Param("planIds") List<Integer> planIds);
}
package com.fitassist.backend.repository;

import com.fitassist.backend.model.plan.Plan;
import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

import static com.fitassist.backend.model.plan.Plan.*;

public interface PlanRepository extends JpaRepository<Plan, Integer>, JpaSpecificationExecutor<Plan> {

	@Query("""
			    SELECT p
			    FROM Plan p
			    JOIN FETCH p.user
			    LEFT JOIN FETCH p.planCategoryAssociations pca
			    LEFT JOIN FETCH pca.planCategory
			    LEFT JOIN FETCH p.planInstructions
			    LEFT JOIN FETCH p.workouts w
			    LEFT JOIN FETCH w.workoutSets ws
			    LEFT JOIN FETCH ws.workoutSetExercises wse
			    LEFT JOIN FETCH wse.exercise e
			    WHERE p.id = :planId
			""")
	Optional<Plan> findByIdWithDetails(@Param("planId") int planId);

	@Override
	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	Page<Plan> findAll(Specification<Plan> spec, @NotNull Pageable pageable);

	@Modifying
	@Query("UPDATE Plan p SET p.views = p.views + 1 WHERE p.id = :planId")
	void incrementViews(@Param("planId") int planId);

	@EntityGraph(value = GRAPH_BASE)
	@Query("SELECT p FROM Plan p WHERE p.isPublic = true")
	List<Plan> findAllWithoutAssociations();

	@EntityGraph(value = GRAPH_SUMMARY)
	@Query("""
			    SELECT p
			    FROM Plan p
			    JOIN p.workouts w
			    JOIN w.workoutSets ws
			    JOIN ws.workoutSetExercises wse
			    WHERE wse.exercise.id = :exerciseId
			      AND p.isPublic = true
			    ORDER BY p.createdAt DESC
			""")
	List<Plan> findByExerciseIdWithDetails(@Param("exerciseId") int exerciseId);

	@EntityGraph(value = GRAPH_SUMMARY)
	@Query("""
			    SELECT p
			    FROM Plan p
			    WHERE p.id IN :planIds
			""")
	List<Plan> findByIdsWithDetails(@Param("planIds") List<Integer> planIds);

}

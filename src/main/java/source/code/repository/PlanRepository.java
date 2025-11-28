package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer>, JpaSpecificationExecutor<Plan>{
    @Modifying
    @Query("UPDATE Plan p SET p.views = p.views + 1 WHERE p.id = :planId")
    void incrementViews(@Param("planId") Integer planId);

    @EntityGraph(value = "Plan.withoutAssociations")
    @Query("SELECT p FROM Plan p WHERE p.isPublic = true")
    List<Plan> findAllWithoutAssociations();

    @EntityGraph(attributePaths = {"user", "planType", "planCategoryAssociations.planCategory"})
    @Query("""
        SELECT p FROM Plan p WHERE
        (:isPrivate IS NULL AND p.isPublic = true) OR
        (:isPrivate = false AND p.isPublic = true) OR
        (:isPrivate = true AND p.user.id = :userId)
    """)
    List<Plan> findAllWithAssociations(@Param("isPrivate") Boolean isPrivate, @Param("userId") int userId);

    @Query("""
      SELECT DISTINCT p
      FROM Plan p
      LEFT JOIN FETCH p.planCategoryAssociations pca
      LEFT JOIN FETCH pca.planCategory
      LEFT JOIN FETCH p.mediaList
      LEFT JOIN FETCH p.user u
      LEFT JOIN FETCH p.planType
      JOIN p.workouts w
      JOIN w.workoutSetGroups wsg
      JOIN wsg.workoutSets ws
      WHERE ws.exercise.id = :exerciseId
          AND p.isPublic = true
      ORDER BY p.createdAt DESC
    """)
    List<Plan> findByExerciseIdWithDetails(@Param("exerciseId") int exerciseId);

    @Query(value = """
      SELECT DISTINCT p
      FROM Plan p
      LEFT JOIN FETCH p.planCategoryAssociations pca
      LEFT JOIN FETCH pca.planCategory
      LEFT JOIN FETCH p.mediaList
      LEFT JOIN FETCH p.user u
      LEFT JOIN FETCH p.planType
      WHERE ((:isOwnProfile = false OR :isOwnProfile IS NULL) AND p.isPublic = true AND p.user.id = :userId) OR
            (:isOwnProfile = true AND p.user.id = :userId)
    """)
    Page<Plan> findCreatedByUserWithDetails(@Param("userId") int userId, @Param("isOwnProfile") boolean isOwnProfile, Pageable pageable);

    @Query(value = """
      SELECT DISTINCT p
      FROM Plan p
      LEFT JOIN FETCH p.planCategoryAssociations pca
      LEFT JOIN FETCH pca.planCategory
      LEFT JOIN FETCH p.mediaList
      LEFT JOIN FETCH p.user u
      LEFT JOIN FETCH p.planType
      WHERE p.id IN :planIds
    """)
    List<Plan> findByIdsWithDetails(@Param("planIds") List<Integer> planIds);
}
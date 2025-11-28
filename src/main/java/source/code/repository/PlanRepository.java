package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.model.plan.Plan;
import source.code.model.user.TypeOfInteraction;

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
      SELECT new source.code.dto.response.plan.PlanSummaryDto(
             p.id,
             p.name,
             p.description,
             p.isPublic,
             p.user.username,
             p.user.id,
             (SELECT m.imageName FROM Media m
              WHERE m.parentId = p.user.id
              AND m.parentType = 'USER'
              ORDER BY m.id ASC
              LIMIT 1),
             (SELECT m.imageName FROM Media m
              WHERE m.parentId = p.id
              AND m.parentType = 'PLAN'
              ORDER BY m.id ASC
              LIMIT 1),
             null,
             null,
             CAST((SELECT COUNT(up1) FROM UserPlan up1 WHERE up1.plan.id = p.id AND up1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(up2) FROM UserPlan up2 WHERE up2.plan.id = p.id AND up2.type = 'SAVE') AS int),
             p.views,
             new source.code.dto.pojo.PlanTypeShortDto(p.planType.id, p.planType.name),
             p.createdAt,
             null)
      FROM Plan p
      JOIN p.workouts w
      JOIN w.workoutSetGroups wsg
      JOIN wsg.workoutSets ws
      WHERE ws.exercise.id = :exerciseId
      ORDER BY p.createdAt DESC
    """)
    List<PlanSummaryDto> findPlanSummariesByExerciseId(@Param("exerciseId") int exerciseId);

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
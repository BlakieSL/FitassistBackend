package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.dto.response.category.EquipmentResponseDto;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanRepository
        extends JpaRepository<Plan, Integer>, JpaSpecificationExecutor<Plan>{
    @EntityGraph(value = "Plan.withoutAssociations")
    @Query("SELECT p FROM Plan p WHERE p.isPublic = true")
    List<Plan> findAllWithoutAssociations();

    @EntityGraph(attributePaths = {"user", "planType", "planCategoryAssociations.planCategory"})
    @Query("SELECT p FROM Plan p WHERE " +
            "(:isPrivate IS NULL AND p.isPublic = true) OR " +
            "(:isPrivate = false AND p.isPublic = true) OR " +
            "(:isPrivate = true AND p.user.id = :userId)")
    List<Plan> findAllWithAssociations(
            @Param("isPrivate") Boolean isPrivate,
            @Param("userId") int userId
    );

    @Query("SELECT DISTINCT new source.code.dto.response.category.EquipmentResponseDto(e.id, e.name) " +
            "FROM Equipment e " +
            "JOIN e.exercises ex " +
            "JOIN ex.workoutSets ws " +
            "JOIN ws.workoutSetGroup wsg " +
            "JOIN wsg.workout w " +
            "JOIN w.plan p " +
            "WHERE p.id = :planId")
    List<EquipmentResponseDto> findAllEquipmentByPlanId(@Param("planId") int planId);

    @Query("""
        SELECT p FROM Plan p
        WHERE ((:isPrivate IS NULL OR :isPrivate = false) AND (p.isPublic = true AND p.user.id = :userId)) OR
              (:isPrivate = true AND p.user.id = :userId)
""")
    List<Plan> findAllByUser_Id(@Param("isPrivate") Boolean isPrivate,
                                @Param("userId") int userId);
}
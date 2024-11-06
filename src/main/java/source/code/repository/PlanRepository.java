package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.dto.Response.category.EquipmentResponseDto;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanRepository
        extends JpaRepository<Plan, Integer>, JpaSpecificationExecutor<Plan> {
    @EntityGraph(value = "Plan.withoutAssociations")
    @Query("SELECT p FROM Plan p")
    List<Plan> findAllWithoutAssociations();

    @Query("SELECT DISTINCT new source.code.dto.Response.category.EquipmentResponseDto(e.id, e.name) " +
            "FROM Equipment e " +
            "JOIN e.exercises ex " +
            "JOIN ex.workoutSets ws " +
            "JOIN ws.workout w " +
            "JOIN w.plan p " +
            "WHERE p.id = :planId")
    List<EquipmentResponseDto> findAllEquipmentByPlanId(@Param("planId") int planId);
}
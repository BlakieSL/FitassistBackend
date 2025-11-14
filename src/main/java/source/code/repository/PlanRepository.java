package source.code.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.dto.response.category.EquipmentResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.model.plan.Plan;
import source.code.model.user.User;

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

    @Query("""
        SELECT p FROM Plan p
        WHERE ((:isPrivate IS NULL OR :isPrivate = false) AND (p.isPublic = true AND p.user.id = :userId)) OR
              (:isPrivate = true AND p.user.id = :userId)
""")
    List<Plan> findAllByUser_Id(@Param("isPrivate") Boolean isPrivate,
                                @Param("userId") int userId);

    @Query("""
      SELECT new source.code.dto.response.plan.PlanSummaryDto(
             p.id,
             p.name,
             p.description,
             p.isPublic,
             p.user.username,
             CAST((SELECT COUNT(up1) FROM UserPlan up1 WHERE up1.plan.id = p.id AND up1.type = 'LIKE') AS int),
             CAST((SELECT COUNT(up2) FROM UserPlan up2 WHERE up2.plan.id = p.id AND up2.type = 'SAVE') AS int))
      FROM Plan p
      WHERE ((:isOwnProfile IS NULL OR :isOwnProfile = false) AND (p.isPublic = true AND p.user.id = :userId)) OR
            (:isOwnProfile = true AND p.user.id = :userId)
    """)
    List<PlanSummaryDto> findSummaryByUserId(@Param("isOwnProfile") Boolean isOwnProfile,
                                             @Param("userId") Integer userId);

    List<Plan> user(@NotNull User user);
}
package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserPlan;

import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Integer> {
    @Query("""
        SELECT up FROM UserPlan up
        WHERE up.plan.isPublic = true AND up.user.id = :userId AND up.type = :type
""")
    List<UserPlan> findByUserIdAndType(@Param("userId") int userId,
                                       @Param("type") TypeOfInteraction type);

    @Query("""
           SELECT new source.code.dto.response.plan.PlanSummaryDto(
               p.id,
               p.name,
               p.description,
               p.isPublic,
               u.username,
               u.id,
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = u.id
                AND m.parentType = 'USER'
                ORDER BY m.id ASC
                LIMIT 1),
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = p.id
                AND m.parentType = 'PLAN'
                ORDER BY m.id ASC
                LIMIT 1),
               null,
               CAST((SELECT COUNT(up2) FROM UserPlan up2 WHERE up2.plan.id = p.id AND up2.type = 'LIKE') AS int),
               CAST((SELECT COUNT(up3) FROM UserPlan up3 WHERE up3.plan.id = p.id AND up3.type = 'SAVE') AS int))
           FROM UserPlan up
           JOIN up.plan p
           JOIN p.user u
           WHERE up.user.id = :userId
           AND up.type = :type
           AND p.isPublic = true
           """)
    List<PlanSummaryDto> findPlanSummaryByUserIdAndType(@Param("userId") int userId, @Param("type") TypeOfInteraction type);

    Optional<UserPlan> findByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    boolean existsByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    long countByPlanIdAndType(int planId, TypeOfInteraction type);
}
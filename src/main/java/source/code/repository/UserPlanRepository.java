package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.plan.PlanCountsProjection;
import source.code.dto.pojo.projection.plan.PlanInteractionDateProjection;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserPlan;

import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Integer> {
    Optional<UserPlan> findByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    boolean existsByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);


    @Query("""
        SELECT
            up.plan.id as planId,
            SUM(CASE WHEN up.type = 'LIKE' THEN 1 ELSE 0 END) as likesCount,
            SUM(CASE WHEN up.type = 'DISLIKE' THEN 1 ELSE 0 END) as dislikesCount,
            SUM(CASE WHEN up.type = 'SAVE' THEN 1 ELSE 0 END) as savesCount
        FROM UserPlan up
        WHERE up.plan.id IN :planIds
        GROUP BY up.plan.id
    """)
    List<PlanCountsProjection> findCountsByPlanIds(@Param("planIds") List<Integer> planIds);

    @Query(value = """
        SELECT up
        FROM UserPlan up
        JOIN FETCH up.plan p
        WHERE up.user.id = :userId
        AND up.type = :type
        AND p.isPublic = true
    """)
    Page<UserPlan> findByUserIdAndTypeWithPlan(
            @Param("userId") int userId,
            @Param("type") TypeOfInteraction type,
            Pageable pageable);
}
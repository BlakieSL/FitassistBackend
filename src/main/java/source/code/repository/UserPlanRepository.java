package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.EntityCountsProjection;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserPlan;

import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Integer> {
    Optional<UserPlan> findByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    boolean existsByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    @Query("""
        SELECT
            p.id as entityId,
            MAX(CASE WHEN up.user.id = :userId AND up.type = 'LIKE' THEN 1 ELSE 0 END) as isLiked,
            MAX(CASE WHEN up.user.id = :userId AND up.type = 'DISLIKE' THEN 1 ELSE 0 END) as isDisliked,
            MAX(CASE WHEN up.user.id = :userId AND up.type = 'SAVE' THEN 1 ELSE 0 END) as isSaved,
            COALESCE(SUM(CASE WHEN up.type = 'LIKE' THEN 1 ELSE 0 END), 0) as likesCount,
            COALESCE(SUM(CASE WHEN up.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) as dislikesCount,
            COALESCE(SUM(CASE WHEN up.type = 'SAVE' THEN 1 ELSE 0 END), 0) as savesCount
        FROM Plan p
        LEFT JOIN UserPlan up ON up.plan.id = p.id
        WHERE p.id = :planId
        GROUP BY p.id
    """)
    EntityCountsProjection findCountsByPlanId(@Param("userId") int userId,
                                              @Param("planId") int planId);

    @Query("""
        SELECT
            p.id as entityId,
            MAX(CASE WHEN up.user.id = :userId AND up.type = 'LIKE' THEN 1 ELSE 0 END) as isLiked,
            MAX(CASE WHEN up.user.id = :userId AND up.type = 'DISLIKE' THEN 1 ELSE 0 END) as isDisliked,
            MAX(CASE WHEN up.user.id = :userId AND up.type = 'SAVE' THEN 1 ELSE 0 END) as isSaved,
            COALESCE(SUM(CASE WHEN up.type = 'LIKE' THEN 1 ELSE 0 END), 0) as likesCount,
            COALESCE(SUM(CASE WHEN up.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) as dislikesCount,
            COALESCE(SUM(CASE WHEN up.type = 'SAVE' THEN 1 ELSE 0 END), 0) as savesCount
        FROM Plan p
        LEFT JOIN UserPlan up ON up.plan.id = p.id
        WHERE p.id IN :planIds
        GROUP BY p.id
    """)
    List<EntityCountsProjection> findCountsByPlanIds(@Param("userId") int userId,
                                                     @Param("planIds") List<Integer> planIds);

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
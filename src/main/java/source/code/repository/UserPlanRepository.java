package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<UserPlan> findByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    boolean existsByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    long countByPlanIdAndType(int planId, TypeOfInteraction type);
}
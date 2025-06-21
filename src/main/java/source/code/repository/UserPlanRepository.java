package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.UserPlan;

import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Integer> {
    List<UserPlan> findByUserIdAndType(int userId, TypeOfInteraction type);

    Optional<UserPlan> findByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    boolean existsByUserIdAndPlanIdAndType(int userId, int planId, TypeOfInteraction type);

    long countByPlanIdAndType(int planId, TypeOfInteraction type);
}
package source.code.repository;

import source.code.model.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
    List<UserPlan> findByUserId(int userId);
    Optional<UserPlan> findByUserIdAndPlanIdAndType(int userId, int planId, short type);
    boolean existsByUserIdAndPlanIdAndType(int userId, int planId, short type);
    long countByPlanIdAndType(int planId, short type);
}
package source.code.repository;

import source.code.model.UserFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {
    boolean existsByUserIdAndFoodIdAndType(int userId, int foodId, short type);
    Optional<UserFood> findByUserIdAndFoodIdAndType(int userId, int foodId, short type);
    List<UserFood> findByUserId(int userId);
    long countByFoodIdAndType(int foodId, short type);
}
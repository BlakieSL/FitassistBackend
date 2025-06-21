package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserFood;

import java.util.List;
import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {
    boolean existsByUserIdAndFoodId(int userId, int foodId);

    Optional<UserFood> findByUserIdAndFoodId(int userId, int foodId);

    List<UserFood> findByUserId(int userId);

    long countByFoodId(int foodId);
}
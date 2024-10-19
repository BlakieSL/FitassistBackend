package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Food.DailyFoodItem;

import java.util.Optional;

public interface DailyFoodItemRepository extends JpaRepository<DailyFoodItem, Integer> {
  Optional<DailyFoodItem> findByDailyFoodIdAndFoodId(int dailyFoodId, int foodId);
}
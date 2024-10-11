package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.DailyFoodItem;

public interface DailyCartFoodRepository extends JpaRepository<DailyFoodItem, Integer> {
}
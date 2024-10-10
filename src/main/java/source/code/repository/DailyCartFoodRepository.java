package source.code.repository;

import source.code.model.DailyFoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartFoodRepository extends JpaRepository<DailyFoodItem, Integer> {
}
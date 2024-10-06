package source.code.repository;

import source.code.model.DailyCartFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartFoodRepository extends JpaRepository<DailyCartFood, Integer> {
}
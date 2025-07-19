package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.daily.DailyCartFood;

import java.util.Optional;

public interface DailyCartFoodRepository extends JpaRepository<DailyCartFood, Integer> {
    Optional<DailyCartFood> findByDailyCartIdAndFoodId(int dailyCartId, int foodId);
}
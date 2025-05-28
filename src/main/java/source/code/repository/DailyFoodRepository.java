package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.food.DailyFood;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyFoodRepository extends JpaRepository<DailyFood, Integer> {
    @EntityGraph(attributePaths = {"dailyFoodItems"})
    Optional<DailyFood> findByUserId(int id);

    @EntityGraph(attributePaths = {"dailyFoodItems"})
    Optional<DailyFood> findByUserIdAndDate(int userId, LocalDate date);
}
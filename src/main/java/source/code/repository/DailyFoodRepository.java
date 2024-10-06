package source.code.repository;

import source.code.model.DailyFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyFoodRepository extends JpaRepository<DailyFood, Integer> {
    Optional<DailyFood> findByUserId(int id);

    Optional<DailyFood> findByUserIdAndDate(int id, LocalDate date);

    void removeDailyCartByUserId(int userId);
}
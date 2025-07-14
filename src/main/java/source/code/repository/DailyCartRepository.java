package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.daily.DailyCart;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
    Optional<DailyCart> findByUserIdAndDate(int userId, LocalDate date);
}

package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.activity.DailyActivity;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
    @EntityGraph(attributePaths = {"dailyActivityItems"})
    Optional<DailyActivity> findByUserId(int userId);

    @EntityGraph(attributePaths = {"dailyActivityItems"})
    Optional<DailyActivity> findByUserIdAndDate(int id, LocalDate date);
}
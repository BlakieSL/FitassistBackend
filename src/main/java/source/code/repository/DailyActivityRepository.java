package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.activity.DailyActivity;

import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
    @EntityGraph(attributePaths = {"dailyActivityItems"})
    Optional<DailyActivity> findByUserId(int userId);
}
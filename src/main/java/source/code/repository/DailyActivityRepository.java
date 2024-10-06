package source.code.repository;

import source.code.model.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
    Optional<DailyActivity> findByUserId(int userId);
}
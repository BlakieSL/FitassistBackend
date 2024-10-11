package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.DailyActivity;

import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
  Optional<DailyActivity> findByUserId(int userId);
}
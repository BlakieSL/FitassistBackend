package source.code.repository;

import source.code.model.DailyCartActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartActivityRepository extends JpaRepository<DailyCartActivity, Integer> {
}
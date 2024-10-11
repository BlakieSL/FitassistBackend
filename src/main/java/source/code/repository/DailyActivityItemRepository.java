package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.DailyActivityItem;

public interface DailyActivityItemRepository extends JpaRepository<DailyActivityItem, Integer> {
}
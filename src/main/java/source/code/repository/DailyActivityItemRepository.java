package source.code.repository;

import source.code.model.DailyActivityItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyActivityItemRepository extends JpaRepository<DailyActivityItem, Integer> {
}
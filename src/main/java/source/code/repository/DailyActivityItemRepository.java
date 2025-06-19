package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.daily.DailyActivityItem;

import java.util.Optional;

public interface DailyActivityItemRepository extends JpaRepository<DailyActivityItem, Integer> {
    Optional<DailyActivityItem> findByDailyCartIdAndActivityId(int dailyActivityId, int activityId);
}
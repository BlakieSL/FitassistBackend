package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.activity.DailyActivityItem;

import java.util.Optional;

public interface DailyActivityItemRepository extends JpaRepository<DailyActivityItem, Integer> {
    Optional<DailyActivityItem> findByDailyActivityIdAndActivityId(int dailyActivityId, int activityId);

    boolean existsByIdAndDailyActivityId(int dailyActivityItemId, int dailyActivityId);
}
package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.daily.DailyCartActivity;

import java.util.Optional;

public interface DailyCartActivityRepository extends JpaRepository<DailyCartActivity, Integer> {
    Optional<DailyCartActivity> findByDailyCartIdAndActivityId(int dailyCartId, int activityId);

    @Query("""
            SELECT dca FROM DailyCartActivity dca
            LEFT JOIN FETCH dca.dailyCart dc
            LEFT JOIN FETCH dc.user u
            WHERE dca.id = :id
        """)
    @EntityGraph(value = "DailyCartActivity.withoutAssociations")
    Optional<DailyCartActivity> findByIdWithUser(int id);

    @Query("SELECT dca FROM DailyCartActivity dca WHERE dca.id = :id")
    @EntityGraph(value = "DailyCartActivity.withoutAssociations")
    Optional<DailyCartActivity> findByIdWithoutAssociations(int id);
}
package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.daily.DailyCart;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
    @EntityGraph(value = "DailyCart.withoutAssociations")
    Optional<DailyCart> findByUserIdAndDate(int userId, LocalDate date);


    @Query("""
            SELECT dc
            FROM DailyCart dc
            LEFT JOIN FETCH dc.user u
            LEFT JOIN FETCH dc.dailyCartActivities dca
            LEFT JOIN FETCH dca.activity a
            LEFT JOIN FETCH a.activityCategory
            WHERE dc.user.id = :userId AND dc.date = :date
    """)
    Optional<DailyCart> findByUserIdAndDateWithAssociations(int userId, LocalDate date);
}

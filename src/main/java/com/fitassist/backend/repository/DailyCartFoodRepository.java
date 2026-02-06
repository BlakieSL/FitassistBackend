package com.fitassist.backend.repository;

import com.fitassist.backend.model.daily.DailyCartFood;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static com.fitassist.backend.model.daily.DailyCartFood.*;

public interface DailyCartFoodRepository extends JpaRepository<DailyCartFood, Integer> {

	Optional<DailyCartFood> findByDailyCartIdAndFoodId(int dailyCartId, int foodId);

	@EntityGraph(value = GRAPH_BASE)
	@Query("""
			    SELECT dcf FROM DailyCartFood dcf
			    LEFT JOIN FETCH dcf.dailyCart dc
			    LEFT JOIN FETCH dc.user u
			    WHERE dcf.id = :id
			""")
	Optional<DailyCartFood> findByIdWithUser(int id);

	@EntityGraph(value = GRAPH_BASE)
	@Query("SELECT dcf FROM DailyCartFood dcf WHERE dcf.id = :id")
	Optional<DailyCartFood> findByIdWithoutAssociations(int id);

}

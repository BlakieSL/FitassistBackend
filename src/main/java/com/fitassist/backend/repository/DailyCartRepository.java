package com.fitassist.backend.repository;

import com.fitassist.backend.dto.pojo.DateFoodMacros;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.pojo.projection.UserActionCountsProjection;
import com.fitassist.backend.model.daily.DailyCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.fitassist.backend.model.daily.DailyCart.GRAPH_BASE;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {

	@EntityGraph(value = GRAPH_BASE)
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
	Optional<DailyCart> findByUserIdAndDateWithActivityAssociations(int userId, LocalDate date);

	@Query("""
			    SELECT dc
			    FROM DailyCart dc
			    LEFT JOIN FETCH dc.user u
			    LEFT JOIN FETCH dc.dailyCartFoods dcf
			    LEFT JOIN FETCH dcf.food f
			    LEFT JOIN FETCH f.foodCategory
			    WHERE dc.user.id = :userId AND dc.date = :date
			""")
	Optional<DailyCart> findByUserIdAndDateWithFoodAssociations(int userId, LocalDate date);

	@Query("""
			    SELECT new com.fitassist.backend.dto.pojo.FoodMacros(
			        CAST(COALESCE(SUM(f.calories * dcf.quantity / 100), 0) AS bigdecimal),
			        CAST(COALESCE(SUM(f.protein * dcf.quantity / 100), 0) AS bigdecimal),
			        CAST(COALESCE(SUM(f.fat * dcf.quantity / 100), 0) AS bigdecimal),
			        CAST(COALESCE(SUM(f.carbohydrates * dcf.quantity / 100), 0) AS bigdecimal)
			    )
			    FROM DailyCart dc
			    LEFT JOIN dc.dailyCartFoods dcf
			    LEFT JOIN dcf.food f
			    WHERE dc.user.id = :userId AND dc.date = :date
			""")
	Optional<FoodMacros> findAggregatedFoodMacrosByUserIdAndDate(int userId, LocalDate date);

	@Query("""
			    SELECT new com.fitassist.backend.dto.pojo.DateFoodMacros(
			        dc.date,
			        CAST(COALESCE(SUM(f.calories * dcf.quantity / 100), 0) AS bigdecimal),
			        CAST(COALESCE(SUM(f.protein * dcf.quantity / 100), 0) AS bigdecimal),
			        CAST(COALESCE(SUM(f.fat * dcf.quantity / 100), 0) AS bigdecimal),
			        CAST(COALESCE(SUM(f.carbohydrates * dcf.quantity / 100), 0) AS bigdecimal)
			    )
			    FROM DailyCart dc
			    LEFT JOIN dc.dailyCartFoods dcf
			    LEFT JOIN dcf.food f
			    WHERE dc.user.id = :userId AND dc.date BETWEEN :startDate AND :endDate
			    GROUP BY dc.date
			""")
	List<DateFoodMacros> findAggregatedFoodMacrosByUserIdAndDateRange(int userId, LocalDate startDate,
			LocalDate endDate);

	@Query("""
			    SELECT dc
			    FROM DailyCart dc
			    LEFT JOIN FETCH dc.user u
			    LEFT JOIN FETCH dc.dailyCartActivities dca
			    LEFT JOIN FETCH dca.activity a
			    LEFT JOIN FETCH a.activityCategory
			    WHERE dc.user.id = :userId AND dc.date BETWEEN :startDate AND :endDate
			""")
	List<DailyCart> findByUserIdAndDateRangeWithActivityAssociations(int userId, LocalDate startDate,
			LocalDate endDate);

	@Query("""
			    SELECT dc.date AS date,
			        COUNT(DISTINCT dcf.id) AS foodLogsCount,
			        COUNT(DISTINCT dca.id) AS activityLogsCount
			    FROM DailyCart dc
			    LEFT JOIN dc.dailyCartFoods dcf
			    LEFT JOIN dc.dailyCartActivities dca
			    WHERE dc.user.id = :userId AND dc.date BETWEEN :startDate AND :endDate
			    GROUP BY dc.date
			""")
	List<UserActionCountsProjection> findActionCountsByUserIdAndDateRange(int userId, LocalDate startDate,
			LocalDate endDate);

}

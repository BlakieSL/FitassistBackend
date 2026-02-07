package com.fitassist.backend.repository;

import com.fitassist.backend.dto.pojo.projection.SavesProjection;
import com.fitassist.backend.model.user.interactions.UserFood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {

	boolean existsByUserIdAndFoodId(int userId, int foodId);

	Optional<UserFood> findByUserIdAndFoodId(int userId, int foodId);

	long countByFoodId(int foodId);

	@Query("""
			    SELECT COUNT(uf) as savesCount,
			           SUM(CASE WHEN uf.user.id = :userId THEN 1 ELSE 0 END) as userSaved
			    FROM UserFood uf
			    WHERE uf.food.id = :foodId
			""")
	SavesProjection findCountsAndInteractionsByFoodId(@Param("foodId") int foodId, @Param("userId") int userId);

	@Query("""
			    SELECT uf.food.id as entityId,
			           COUNT(uf) as savesCount,
			           SUM(CASE WHEN uf.user.id = :userId THEN 1 ELSE 0 END) as userSaved
			    FROM UserFood uf
			    WHERE uf.food.id IN :foodIds
			    GROUP BY uf.food.id
			""")
	List<SavesProjection> findCountsAndInteractionsByFoodIds(@Param("userId") int userId,
			@Param("foodIds") List<Integer> foodIds);

	@Query(value = """
			SELECT uf FROM UserFood uf
			JOIN FETCH uf.food f
			JOIN FETCH f.foodCategory
			LEFT JOIN FETCH f.mediaList
			WHERE uf.user.id = :userId
			""")
	Page<UserFood> findAllByUserIdWithMedia(@Param("userId") int userId, Pageable pageable);

}

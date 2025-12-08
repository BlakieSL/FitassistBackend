package source.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.model.user.UserFood;

import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {
    boolean existsByUserIdAndFoodId(int userId, int foodId);

    Optional<UserFood> findByUserIdAndFoodId(int userId, int foodId);

    @Query("""
        SELECT COUNT(uf) as savesCount,
               SUM(CASE WHEN uf.user.id = :userId THEN 1 ELSE 0 END) as userSaved
        FROM UserFood uf
        WHERE uf.food.id = :foodId
    """)
    SavesProjection findSavesCountAndUserSaved(@Param("foodId") int foodId, @Param("userId") int userId);

    @Query(value = """
           SELECT uf FROM UserFood uf
           JOIN FETCH uf.food f
           JOIN FETCH f.foodCategory
           LEFT JOIN FETCH f.mediaList
           WHERE uf.user.id = :userId
           """)
    Page<UserFood> findAllByUserIdWithMedia(@Param("userId") int userId, Pageable pageable);
}
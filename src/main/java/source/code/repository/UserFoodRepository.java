package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.food.FoodResponseDto;
import source.code.model.user.UserFood;

import java.util.List;
import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {
    boolean existsByUserIdAndFoodId(int userId, int foodId);

    Optional<UserFood> findByUserIdAndFoodId(int userId, int foodId);

    List<UserFood> findByUserId(int userId);

    @Query("""
           SELECT new source.code.dto.response.food.FoodResponseDto(
               f.id,
               f.name,
               f.calories,
               f.protein,
               f.fat,
               f.carbohydrates,
               fc.id,
               fc.name,
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = f.id
                AND m.parentType = 'FOOD'
                ORDER BY m.id ASC
                LIMIT 1),
               null,
               uf.createdAt)
           FROM UserFood uf
           JOIN uf.food f
           JOIN f.foodCategory fc
           WHERE uf.user.id = :userId
           ORDER BY uf.createdAt DESC
           """)
    List<FoodResponseDto> findFoodDtosByUserId(@Param("userId") int userId);

    long countByFoodId(int foodId);
}
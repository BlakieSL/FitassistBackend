package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.food.Food;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository
        extends JpaRepository<Food, Integer>, JpaSpecificationExecutor<Food> {
    @EntityGraph(value = "Food.withoutAssociations")
    @Query("SELECT f FROM Food f")
    List<Food> findAllWithoutAssociations();

    @Query("SELECT f FROM Food f " +
           "LEFT JOIN FETCH f.foodCategory " +
           "LEFT JOIN FETCH f.mediaList " +
           "LEFT JOIN FETCH f.userFoods " +
           "WHERE f.id = :id")
    Optional<Food> findByIdWithMedia(@Param("id") int id);
    
    @Query("SELECT COUNT(uf) FROM UserFood uf WHERE uf.food.id = :foodId")
    int countSavesByFoodId(@Param("foodId") int foodId);
}
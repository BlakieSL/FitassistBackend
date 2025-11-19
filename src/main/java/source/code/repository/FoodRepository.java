package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.food.Food;

import java.util.List;
import java.util.Optional;

public interface FoodRepository
        extends JpaRepository<Food, Integer>, JpaSpecificationExecutor<Food> {
    List<Food> findAllByFoodCategory_Id(int categoryId);

    @EntityGraph(value = "Food.withoutAssociations")
    @Query("SELECT f FROM Food f")
    List<Food> findAllWithoutAssociations();

    @Query("SELECT f FROM Food f " +
           "LEFT JOIN FETCH f.foodCategory " +
           "LEFT JOIN FETCH f.mediaList " +
           "WHERE f.id = :id")
    Optional<Food> findByIdWithMedia(int id);
}
package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.Exercise.Exercise;
import source.code.model.Food.Food;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {
  List<Food> findAllByFoodCategory_Id(int categoryId);
  @EntityGraph(value = "Food.withoutAssociations")
  @Query("SELECT f FROM Food f")
  List<Food> findAllWithoutAssociations();
}
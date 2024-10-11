package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Food;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {
  List<Food> findAllByNameContainingIgnoreCase(String name);

  List<Food> findAllByFoodCategory_Id(int categoryId);
}
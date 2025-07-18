package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.food.FoodCategory;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {
    boolean existsByIdAndFoodsIsNotEmpty(Integer id);
}
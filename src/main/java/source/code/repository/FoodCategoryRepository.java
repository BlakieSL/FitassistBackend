package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Food.FoodCategory;

import java.util.Optional;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {
    Optional<FoodCategory> findByName(String name);
}
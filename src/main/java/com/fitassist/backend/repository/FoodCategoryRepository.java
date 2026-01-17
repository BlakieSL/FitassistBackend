package com.fitassist.backend.repository;

import com.fitassist.backend.model.food.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {

	boolean existsByIdAndFoodsIsNotEmpty(Integer id);

}

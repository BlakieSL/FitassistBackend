package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.food.FoodCategory;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {

	boolean existsByIdAndFoodsIsNotEmpty(Integer id);

}

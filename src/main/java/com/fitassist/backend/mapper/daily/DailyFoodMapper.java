package com.fitassist.backend.mapper.daily;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.model.daily.DailyCartFood;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.food.FoodCategory;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public abstract class DailyFoodMapper {

	@Mapping(target = "dailyItemId", source = "id")
	@Mapping(target = "id", source = "food.id")
	@Mapping(target = "name", source = "food.name")
	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "food.foodCategory", qualifiedByName = "mapFoodCategoryToResponseDto")
	public abstract FoodCalculatedMacrosResponseDto toFoodCalculatedMacrosResponseDto(DailyCartFood dailyCartFood);

	@AfterMapping
	protected void setCalculatedMacros(@MappingTarget FoodCalculatedMacrosResponseDto responseDto,
			DailyCartFood dailyCartFood) {
		Food food = dailyCartFood.getFood();
		BigDecimal quantity = dailyCartFood.getQuantity();
		BigDecimal divisor = new BigDecimal("100");
		BigDecimal factor = quantity.divide(divisor, 10, RoundingMode.HALF_UP);

		BigDecimal calories = food.getCalories().multiply(factor).setScale(1, RoundingMode.HALF_UP);
		BigDecimal protein = food.getProtein().multiply(factor).setScale(2, RoundingMode.HALF_UP);
		BigDecimal fat = food.getFat().multiply(factor).setScale(2, RoundingMode.HALF_UP);
		BigDecimal carbohydrates = food.getCarbohydrates().multiply(factor).setScale(2, RoundingMode.HALF_UP);

		responseDto.setFoodMacros(FoodMacros.of(calories, protein, fat, carbohydrates));
	}

	@Named("mapFoodCategoryToResponseDto")
	protected CategoryResponseDto mapFoodCategoryToResponseDto(FoodCategory category) {
		return new CategoryResponseDto(category.getId(), category.getName());
	}

}

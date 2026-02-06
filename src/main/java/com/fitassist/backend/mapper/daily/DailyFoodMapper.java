package com.fitassist.backend.mapper.daily;

import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.model.daily.DailyCartFood;
import com.fitassist.backend.model.food.FoodCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class DailyFoodMapper {

	@Mapping(target = "dailyItemId", source = "id")
	@Mapping(target = "id", source = "food.id")
	@Mapping(target = "name", source = "food.name")
	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "food.foodCategory", qualifiedByName = "mapCategoryToResponse")
	public abstract FoodCalculatedMacrosResponseDto toResponse(DailyCartFood dailyCartFood);

	@Named("mapCategoryToResponse")
	protected CategoryResponseDto mapCategoryToResponse(FoodCategory category) {
		return new CategoryResponseDto(category.getId(), category.getName());
	}

}

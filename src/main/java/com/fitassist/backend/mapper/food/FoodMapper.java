package com.fitassist.backend.mapper.food;

import com.fitassist.backend.dto.pojo.DateFoodMacros;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.request.food.FoodCreateDto;
import com.fitassist.backend.dto.request.food.FoodUpdateDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.dto.response.food.FoodResponseDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.mapper.CommonMappingHelper;
import com.fitassist.backend.model.food.Food;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class FoodMapper {

	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "foodCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "images", source = "mediaList", qualifiedByName = "mapMediaListToImagesDto")
	@Mapping(target = "recipes", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract FoodResponseDto toResponse(Food food);

	@AfterMapping
	protected void setFoodMacros(@MappingTarget FoodResponseDto dto, Food food) {
		dto.setFoodMacros(FoodSummaryDto.createFoodMacros(food));
	}

	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "foodCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract FoodSummaryDto toSummary(Food food);

	@AfterMapping
	protected void setFoodMacros(@MappingTarget FoodSummaryDto dto, Food food) {
		dto.setFoodMacros(FoodSummaryDto.createFoodMacros(food));
	}

	@Mapping(target = "category", source = "foodCategory", qualifiedByName = "mapCategoryToResponse")
	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "quantity", ignore = true)
	@Mapping(target = "dailyItemId", ignore = true)
	public abstract FoodCalculatedMacrosResponseDto toCalculated(Food food);

	public abstract FoodMacros toFoodMacros(DateFoodMacros dateFoodMacros);

	@Mapping(target = "foodCategory", expression = "java(context.getCategory())")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartFoods", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "userFoods", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Food toEntity(FoodCreateDto dto, @Context FoodMappingContext context);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "foodCategory", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartFoods", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "userFoods", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void update(@MappingTarget Food food, FoodUpdateDto request, @Context FoodMappingContext context);

	@AfterMapping
	protected void updateFoodCategory(@MappingTarget Food food, FoodUpdateDto dto,
			@Context FoodMappingContext context) {
		if (dto.getCategoryId() != null) {
			food.setFoodCategory(context.getCategory());
		}
	}

}

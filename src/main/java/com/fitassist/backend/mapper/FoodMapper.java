package com.fitassist.backend.mapper;

import com.fitassist.backend.dto.pojo.DateFoodMacros;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.request.food.FoodCreateDto;
import com.fitassist.backend.dto.request.food.FoodUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
import com.fitassist.backend.dto.response.food.FoodResponseDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.mapper.helper.CommonMappingHelper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.food.FoodCategory;
import com.fitassist.backend.repository.FoodCategoryRepository;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Mapper(componentModel = "spring", uses = { CommonMappingHelper.class })
public abstract class FoodMapper {

	@Autowired
	private RepositoryHelper repositoryHelper;

	@Autowired
	private FoodCategoryRepository foodCategoryRepository;

	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "foodCategory", qualifiedByName = "mapFoodCategoryToResponseDto")
	@Mapping(target = "imageName", source = "mediaList", qualifiedByName = "mapMediaToFirstImageName")
	@Mapping(target = "firstImageUrl", ignore = true)
	@Mapping(target = "interactionCreatedAt", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract FoodSummaryDto toSummaryDto(Food food);

	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "foodCategory", qualifiedByName = "mapFoodCategoryToResponseDto")
	@Mapping(target = "quantity", expression = "java(factor.multiply(new BigDecimal(100)))")
	@Mapping(target = "dailyItemId", ignore = true)
	public abstract FoodCalculatedMacrosResponseDto toDtoWithFactor(Food food, @Context BigDecimal factor);

	@Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartFoods", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "userFoods", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract Food toEntity(FoodCreateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dailyCartFoods", ignore = true)
	@Mapping(target = "recipeFoods", ignore = true)
	@Mapping(target = "userFoods", ignore = true)
	@Mapping(target = "mediaList", ignore = true)
	public abstract void updateFood(@MappingTarget Food food, FoodUpdateDto request);

	@Mapping(target = "foodMacros", ignore = true)
	@Mapping(target = "category", source = "foodCategory", qualifiedByName = "mapFoodCategoryToResponseDto")
	@Mapping(target = "images", source = "mediaList", qualifiedByName = "mapMediaListToImagesDto")
	@Mapping(target = "recipes", ignore = true)
	@Mapping(target = "savesCount", ignore = true)
	@Mapping(target = "saved", ignore = true)
	public abstract FoodResponseDto toDetailedResponseDto(Food food);

	public abstract FoodMacros toFoodMacros(DateFoodMacros dateFoodMacros);

	@AfterMapping
	protected void calculateMacros(@MappingTarget FoodCalculatedMacrosResponseDto dto, Food food,
			@Context BigDecimal factor) {
		MathContext mathContext = new MathContext(10, RoundingMode.HALF_UP);

		BigDecimal calories = food.getCalories().multiply(factor, mathContext).setScale(1, RoundingMode.HALF_UP);
		BigDecimal protein = food.getProtein().multiply(factor, mathContext).setScale(1, RoundingMode.HALF_UP);
		BigDecimal fat = food.getFat().multiply(factor, mathContext).setScale(1, RoundingMode.HALF_UP);
		BigDecimal carbohydrates = food.getCarbohydrates()
			.multiply(factor, mathContext)
			.setScale(1, RoundingMode.HALF_UP);

		dto.setFoodMacros(FoodMacros.of(calories, protein, fat, carbohydrates));
	}

	@AfterMapping
	protected void setFoodMacrosForSummary(@MappingTarget FoodSummaryDto dto, Food food) {
		dto.setFoodMacros(createFoodMacros(food));
	}

	@AfterMapping
	protected void setFoodMacrosForDetailed(@MappingTarget FoodResponseDto dto, Food food) {
		dto.setFoodMacros(createFoodMacros(food));
	}

	private FoodMacros createFoodMacros(Food food) {
		return FoodMacros.of(food.getCalories(), food.getProtein(), food.getFat(), food.getCarbohydrates());
	}

	@Named("categoryIdToFoodCategory")
	protected FoodCategory categoryIdToFoodCategory(int categoryId) {
		return repositoryHelper.find(foodCategoryRepository, FoodCategory.class, categoryId);
	}

	@Named("mapFoodCategoryToResponseDto")
	protected CategoryResponseDto mapFoodCategoryToResponseDto(FoodCategory category) {
		return new CategoryResponseDto(category.getId(), category.getName());
	}

}

package source.code.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.FoodCreateDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.FoodCategoryResponseDto;
import source.code.dto.response.FoodResponseDto;
import source.code.model.Food.Food;
import source.code.model.Food.FoodCategory;
import source.code.repository.FoodCategoryRepository;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class FoodMapper {
  @Autowired
  private FoodCategoryRepository foodCategoryRepository;

  @Mapping(target = "categoryName", source = "foodCategory.name")
  @Mapping(target = "categoryId", source = "foodCategory.id")
  public abstract FoodResponseDto toDto(Food food);

  @Mapping(target = "categoryName", source = "foodCategory.name")
  @Mapping(target = "categoryId", source = "foodCategory.id")
  @Mapping(target = "amount", expression = "java((int) (factor * 100))")
  public abstract FoodCalculatedMacrosResponseDto toDtoWithFactor(Food food, @Context double factor);

  @Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "dailyFoodItems", ignore = true)
  @Mapping(target = "recipeFoods", ignore = true)
  @Mapping(target = "userFoods", ignore = true)
  public abstract Food toEntity(FoodCreateDto dto);

  public abstract FoodCategoryResponseDto toCategoryDto(FoodCategory foodCategory);

  @AfterMapping
  protected void calculateMacros(@MappingTarget FoodCalculatedMacrosResponseDto dto, @Context double factor) {
    dto.setCalories(dto.getCalories() * factor);
    dto.setProtein(dto.getProtein() * factor);
    dto.setFat(dto.getFat() * factor);
    dto.setCarbohydrates(dto.getCarbohydrates() * factor);
  }

  @Named("categoryIdToFoodCategory")
  protected FoodCategory categoryIdToFoodCategory(int categoryId) {
    return foodCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food category with id: " + categoryId + " not found"));
  }
}
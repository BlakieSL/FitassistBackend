package source.code.mapper;

import source.code.dto.FoodAdditionDto;
import source.code.dto.FoodCalculatedDto;
import source.code.dto.FoodCategoryDto;
import source.code.dto.FoodDto;
import source.code.model.Food;
import source.code.model.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class FoodMapper {
    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    public abstract FoodDto toDto(Food food);

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    @Mapping(target = "amount", expression = "java((int) (factor * 100))")
    public abstract FoodCalculatedDto toDtoWithFactor(Food food, @Context double factor);

    @Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyCartFoods", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    @Mapping(target = "userFoods", ignore = true)
    public abstract Food toEntity(FoodAdditionDto dto);

    public abstract FoodCategoryDto toCategoryDto(FoodCategory foodCategory);

    @AfterMapping
    protected void calculateMacros(@MappingTarget FoodCalculatedDto dto, @Context double factor) {
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
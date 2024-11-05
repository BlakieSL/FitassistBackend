package source.code.mapper.food;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.Request.Food.FoodCreateDto;
import source.code.dto.Request.Food.FoodUpdateDto;
import source.code.dto.Response.FoodCalculatedMacrosResponseDto;
import source.code.dto.Response.FoodResponseDto;
import source.code.model.Food.Food;
import source.code.model.Food.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import source.code.service.declaration.helpers.RepositoryHelper;

@Mapper(componentModel = "spring")
public abstract class FoodMapper {
    @Autowired
    private RepositoryHelper repositoryHelper;

    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    public abstract FoodResponseDto toResponseDto(Food food);

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyFoodItems", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    @Mapping(target = "userFoods", ignore = true)
    public abstract void updateFood(@MappingTarget Food food, FoodUpdateDto request);

    @AfterMapping
    protected void calculateMacros(@MappingTarget FoodCalculatedMacrosResponseDto dto, @Context double factor) {
        dto.setCalories(dto.getCalories() * factor);
        dto.setProtein(dto.getProtein() * factor);
        dto.setFat(dto.getFat() * factor);
        dto.setCarbohydrates(dto.getCarbohydrates() * factor);
    }

    @Named("categoryIdToFoodCategory")
    protected FoodCategory categoryIdToFoodCategory(int categoryId) {
        return repositoryHelper.find(foodCategoryRepository, FoodCategory.class, categoryId);
    }
}
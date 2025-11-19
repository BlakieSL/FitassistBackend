package source.code.mapper.food;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.request.food.FoodUpdateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.model.food.Food;
import source.code.model.food.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.helpers.RepositoryHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class FoodMapper {
    @Autowired
    private RepositoryHelper repositoryHelper;

    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    @Mapping(target = "firstImageUrl", ignore = true)
    public abstract FoodSummaryDto toSummaryDto(Food food);

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    @Mapping(target = "quantity", expression = "java(factor.multiply(new BigDecimal(100)))")
    public abstract FoodCalculatedMacrosResponseDto toDtoWithFactor(Food food, @Context BigDecimal factor);

    @Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyCartFoods", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    @Mapping(target = "userFoods", ignore = true)
    public abstract Food toEntity(FoodCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dailyCartFoods", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    @Mapping(target = "userFoods", ignore = true)
    public abstract void updateFood(@MappingTarget Food food, FoodUpdateDto request);

    @AfterMapping
    protected void calculateMacros(
            @MappingTarget FoodCalculatedMacrosResponseDto dto,
            @Context BigDecimal factor) {

        MathContext mathContext = new MathContext(10, RoundingMode.HALF_UP);

        dto.setCalories(dto.getCalories().multiply(factor, mathContext)
                .setScale(1, RoundingMode.HALF_UP));

        dto.setProtein(dto.getProtein().multiply(factor, mathContext)
                .setScale(1, RoundingMode.HALF_UP));

        dto.setFat(dto.getFat().multiply(factor, mathContext)
                .setScale(1, RoundingMode.HALF_UP));

        dto.setCarbohydrates(dto.getCarbohydrates().multiply(factor, mathContext)
                .setScale(1, RoundingMode.HALF_UP));
    }

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    @Mapping(target = "imageUrls", ignore = true)
    public abstract FoodResponseDto toDetailedResponseDto(Food food);

    @AfterMapping
    protected void mapImageUrls(@MappingTarget FoodResponseDto dto, Food food) {
        List<String> imageUrls = food.getMediaList().stream()
                .map(media -> awsS3Service.getImage(media.getImageName()))
                .toList();
        dto.setImageUrls(imageUrls);
    }

    @Named("categoryIdToFoodCategory")
    protected FoodCategory categoryIdToFoodCategory(int categoryId) {
        return repositoryHelper.find(foodCategoryRepository, FoodCategory.class, categoryId);
    }
}
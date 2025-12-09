package source.code.mapper.daily;

import org.mapstruct.*;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.daily.DailyFoodsResponseDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.model.daily.DailyCartFood;
import source.code.model.food.Food;
import source.code.model.food.FoodCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DailyFoodMapper {
    @Mapping(target = "id", source = "dailyCartFood.food.id")
    @Mapping(target = "name", source = "dailyCartFood.food.name")
    @Mapping(target = "calories", ignore = true)
    @Mapping(target = "protein", ignore = true)
    @Mapping(target = "fat", ignore = true)
    @Mapping(target = "carbohydrates", ignore = true)
    @Mapping(target = "category", source = "dailyCartFood.food.foodCategory", qualifiedByName = "mapFoodCategoryToResponseDto")
    @Mapping(target = "quantity", source = "dailyCartFood.quantity")
    public abstract FoodCalculatedMacrosResponseDto toFoodCalculatedMacrosResponseDto(
            DailyCartFood dailyCartFood
    );

    @AfterMapping
    protected void setCalculatedMacros(
            @MappingTarget FoodCalculatedMacrosResponseDto responseDto,
            DailyCartFood dailyCartFood
    ) {
        Food food = dailyCartFood.getFood();
        BigDecimal quantity = dailyCartFood.getQuantity();

        BigDecimal divisor = new BigDecimal("100");

        BigDecimal factor = quantity.divide(divisor, 10, RoundingMode.HALF_UP);

        responseDto.setCalories(
                food.getCalories().multiply(factor)
                        .setScale(1, RoundingMode.HALF_UP)
        );
        responseDto.setProtein(
                food.getProtein().multiply(factor)
                        .setScale(1, RoundingMode.HALF_UP)
        );
        responseDto.setFat(
                food.getFat().multiply(factor)
                        .setScale(1, RoundingMode.HALF_UP)
        );
        responseDto.setCarbohydrates(
                food.getCarbohydrates().multiply(factor)
                        .setScale(1, RoundingMode.HALF_UP)
        );
    }

    public DailyFoodsResponseDto toDailyFoodsResponseDto(List<DailyCartFood> dailyCartFoods) {
        return dailyCartFoods.stream()
                .map(this::toFoodCalculatedMacrosResponseDto)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                DailyFoodsResponseDto::create
                        )
                );
    }

    @Named("mapFoodCategoryToResponseDto")
    protected CategoryResponseDto mapFoodCategoryToResponseDto(FoodCategory category) {
        return new CategoryResponseDto(category.getId(), category.getName());
    }
}

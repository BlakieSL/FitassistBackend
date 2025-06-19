package source.code.mapper.daily;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import source.code.dto.response.daily.DailyFoodsResponseDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.model.daily.DailyFoodItem;
import source.code.model.food.Food;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DailyFoodMapper {
    @Mapping(target = "id", source = "dailyFoodItem.food.id")
    @Mapping(target = "name", source = "dailyFoodItem.food.name")
    @Mapping(target = "calories", ignore = true)
    @Mapping(target = "protein", ignore = true)
    @Mapping(target = "fat", ignore = true)
    @Mapping(target = "carbohydrates", ignore = true)
    @Mapping(target = "categoryId", source = "dailyFoodItem.food.foodCategory.id")
    @Mapping(target = "categoryName", source = "dailyFoodItem.food.foodCategory.name")
    @Mapping(target = "quantity", source = "dailyFoodItem.quantity")
    public abstract FoodCalculatedMacrosResponseDto toFoodCalculatedMacrosResponseDto(
            DailyFoodItem dailyFoodItem
    );

    @AfterMapping
    protected void setCalculatedMacros(
            @MappingTarget FoodCalculatedMacrosResponseDto responseDto,
            DailyFoodItem dailyFoodItem
    ) {
        Food food = dailyFoodItem.getFood();
        BigDecimal quantity = dailyFoodItem.getQuantity();

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

    public DailyFoodsResponseDto toDailyFoodsResponseDto(List<DailyFoodItem> dailyFoodItems) {
        return dailyFoodItems.stream()
                .map(this::toFoodCalculatedMacrosResponseDto)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                DailyFoodsResponseDto::create
                        )
                );
    }
}

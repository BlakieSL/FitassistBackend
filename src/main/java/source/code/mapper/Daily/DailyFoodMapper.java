package source.code.mapper.Daily;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import source.code.dto.Response.DailyFoodsResponseDto;
import source.code.dto.Response.FoodCalculatedMacrosResponseDto;
import source.code.model.Food.DailyFoodItem;
import source.code.model.Food.Food;

import java.util.List;

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
    @Mapping(target = "amount", source = "dailyFoodItem.amount")
    public abstract FoodCalculatedMacrosResponseDto toFoodCalculatedMacrosResponseDto(DailyFoodItem dailyFoodItem);

    @AfterMapping
    protected void setCalculatedMacros(
            @MappingTarget FoodCalculatedMacrosResponseDto responseDto,
            DailyFoodItem dailyFoodItem) {

        Food food = dailyFoodItem.getFood();
        double factor = (double) dailyFoodItem.getAmount() / 100;

        responseDto.setCalories(food.getCalories() * factor);
        responseDto.setProtein(food.getProtein() * factor);
        responseDto.setFat(food.getFat() * factor);
        responseDto.setCarbohydrates(food.getCarbohydrates() * factor);
    }

    public DailyFoodsResponseDto toDailyFoodsResponseDto(List<FoodCalculatedMacrosResponseDto> foods) {
        double totalCalories = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getCalories).sum();
        double totalCarbohydrates = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getCarbohydrates).sum();
        double totalProtein = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getProtein).sum();
        double totalFat = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getFat).sum();

        return new DailyFoodsResponseDto(foods, totalCalories, totalCarbohydrates, totalProtein, totalFat);
    }
}

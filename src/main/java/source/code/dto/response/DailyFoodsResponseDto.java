package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.model.food.Food;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyFoodsResponseDto {
    private List<FoodCalculatedMacrosResponseDto> foods;
    private double totalCalories;
    private double totalCarbohydrates;
    private double totalProtein;
    private double totalFat;

    public static DailyFoodsResponseDto of(List<FoodCalculatedMacrosResponseDto> foods) {
        DailyFoodsResponseDto responseDto = new DailyFoodsResponseDto();
        responseDto.setFoods(foods);

        return responseDto;
    }

    public static DailyFoodsResponseDto create(List<FoodCalculatedMacrosResponseDto> foods) {
        double totalCalories = foods.stream()
                .mapToDouble(FoodCalculatedMacrosResponseDto::getCalories)
                .sum();

        double totalCarbohydrates = foods.stream()
                .mapToDouble(FoodCalculatedMacrosResponseDto::getCarbohydrates)
                .sum();

        double totalProtein = foods.stream()
                .mapToDouble(FoodCalculatedMacrosResponseDto::getProtein)
                .sum();

        double totalFat = foods.stream()
                .mapToDouble(FoodCalculatedMacrosResponseDto::getFat)
                .sum();

        return new DailyFoodsResponseDto(
                foods,
                totalCalories,
                totalCarbohydrates,
                totalProtein,
                totalFat
        );
    }
}

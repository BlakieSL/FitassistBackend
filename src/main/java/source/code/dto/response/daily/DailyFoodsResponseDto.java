package source.code.dto.response.daily;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyFoodsResponseDto implements Serializable {
    private List<FoodCalculatedMacrosResponseDto> foods;
    private BigDecimal totalCalories;
    private BigDecimal totalCarbohydrates;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;

    public static DailyFoodsResponseDto of(List<FoodCalculatedMacrosResponseDto> foods) {
        return create(foods);
    }

    public static DailyFoodsResponseDto create(List<FoodCalculatedMacrosResponseDto> foods) {
        BigDecimal totalCalories = foods.stream()
                .map(food -> food.getFoodMacros().getCalories())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.HALF_UP);

        BigDecimal totalCarbohydrates = foods.stream()
                .map(food -> food.getFoodMacros().getCarbohydrates())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.HALF_UP);

        BigDecimal totalProtein = foods.stream()
                .map(food -> food.getFoodMacros().getProtein())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.HALF_UP);

        BigDecimal totalFat = foods.stream()
                .map(food -> food.getFoodMacros().getFat())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.HALF_UP);

        return new DailyFoodsResponseDto(
                foods,
                totalCalories,
                totalCarbohydrates,
                totalProtein,
                totalFat
        );
    }
}
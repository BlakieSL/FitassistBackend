package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
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
}

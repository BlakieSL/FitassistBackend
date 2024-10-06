package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyFoodsResponseDto {
    private List<FoodCalculatedResponseDto> foods;
    private double totalCalories;
    private double totalCarbohydrates;
    private double totalProtein;
    private double totalFat;
}

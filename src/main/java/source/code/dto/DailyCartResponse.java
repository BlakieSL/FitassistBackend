package source.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartResponse {
    private List<FoodCalculatedDto> foods;
    private double totalCalories;
    private double totalCarbohydrates;
    private double totalProtein;
    private double totalFat;
}

package source.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCalculatedDto {
    private Integer id;
    private String name;
    private double calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private int categoryId;
    private String categoryName;
    private int amount;
}

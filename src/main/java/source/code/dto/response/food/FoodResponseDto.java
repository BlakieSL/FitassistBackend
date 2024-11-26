package source.code.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodResponseDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private double calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private int categoryId;
    private String categoryName;
}
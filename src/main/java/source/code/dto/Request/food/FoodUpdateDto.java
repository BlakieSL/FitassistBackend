package source.code.dto.Request.food;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodUpdateDto {
    @Size(max = 50)
    private String name;
    @Positive
    private Double calories;
    @PositiveOrZero
    private Double protein;
    @PositiveOrZero
    private Double fat;
    @PositiveOrZero
    private Double carbohydrates;
    private Integer categoryId;
}

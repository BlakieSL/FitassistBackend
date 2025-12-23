package source.code.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFoodDto {
    private int id;
    private BigDecimal quantity;
    private Integer foodId;
    private String foodName;
    private BigDecimal foodCalories;
    private BigDecimal foodProtein;
    private BigDecimal foodFat;
    private BigDecimal foodCarbohydrates;

}

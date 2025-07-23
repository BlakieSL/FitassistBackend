package source.code.dto.response.food;

import lombok.*;
import source.code.helper.BaseUserEntity;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class FoodResponseDto implements BaseUserEntity {
    private Integer id;
    @ToString.Include
    private String name;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrates;
    private int categoryId;
    private String categoryName;
}
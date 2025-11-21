package source.code.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.recipe.RecipeSummaryDto;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodResponseDto {
    private Integer id;
    private String name;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrates;
    private int categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private List<RecipeSummaryDto> recipes;
    private long savesCount;
    private boolean saved;
}

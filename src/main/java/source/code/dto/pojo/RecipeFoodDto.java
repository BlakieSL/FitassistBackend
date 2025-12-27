package source.code.dto.pojo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;

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

	private CategoryResponseDto foodCategory;

}

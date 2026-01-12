package source.code.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.response.category.CategoryResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCalculatedMacrosResponseDto implements Serializable {

	private Integer dailyItemId;

	private Integer id;

	private String name;

	private FoodMacros foodMacros;

	private CategoryResponseDto category;

	private BigDecimal quantity;

}

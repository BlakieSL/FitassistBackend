package source.code.dto.request.food;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

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
	private BigDecimal calories;

	@PositiveOrZero
	private BigDecimal protein;

	@PositiveOrZero
	private BigDecimal fat;

	@PositiveOrZero
	private BigDecimal carbohydrates;

	private Integer categoryId;

}

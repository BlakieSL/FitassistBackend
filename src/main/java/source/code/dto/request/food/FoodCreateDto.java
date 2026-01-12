package source.code.dto.request.food;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCreateDto {

	@Size(max = 50)
	@NotBlank
	private String name;

	@NotNull
	@Positive
	private BigDecimal calories;

	@NotNull
	@PositiveOrZero
	private BigDecimal protein;

	@NotNull
	@PositiveOrZero
	private BigDecimal fat;

	@NotNull
	@PositiveOrZero
	private BigDecimal carbohydrates;

	@NotNull
	private int categoryId;

}

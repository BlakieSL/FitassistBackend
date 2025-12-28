package source.code.dto.request.recipe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFoodCreateDto {

	@NotNull
	@NotEmpty
	@Valid
	private List<FoodQuantityPair> foods;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FoodQuantityPair {

		@NotNull
		private Integer foodId;

		@NotNull
		@Positive
		private BigDecimal quantity;

	}

}

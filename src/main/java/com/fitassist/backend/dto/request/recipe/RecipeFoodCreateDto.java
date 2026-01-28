package com.fitassist.backend.dto.request.recipe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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
		private int foodId;

		@NotNull
		@Positive
		@Digits(integer = 4, fraction = 2)
		private BigDecimal quantity;

	}

}

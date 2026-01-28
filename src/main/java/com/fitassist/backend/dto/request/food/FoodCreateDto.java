package com.fitassist.backend.dto.request.food;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 1)
	@Max(900)
	private BigDecimal calories;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	private BigDecimal protein;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	private BigDecimal fat;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	private BigDecimal carbohydrates;

	@NotNull
	private int categoryId;

}

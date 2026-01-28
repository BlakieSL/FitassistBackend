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
public class FoodUpdateDto {

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@PositiveOrZero
	@Digits(integer = 3, fraction = 1)
	@Max(900)
	private BigDecimal calories;

	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	private BigDecimal protein;

	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	private BigDecimal fat;

	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	private BigDecimal carbohydrates;

	private Integer categoryId;

}

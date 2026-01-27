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

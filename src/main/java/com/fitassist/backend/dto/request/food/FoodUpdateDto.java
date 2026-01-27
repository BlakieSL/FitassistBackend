package com.fitassist.backend.dto.request.food;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
	private BigDecimal calories;

	@PositiveOrZero
	private BigDecimal protein;

	@PositiveOrZero
	private BigDecimal fat;

	@PositiveOrZero
	private BigDecimal carbohydrates;

	private Integer categoryId;

}

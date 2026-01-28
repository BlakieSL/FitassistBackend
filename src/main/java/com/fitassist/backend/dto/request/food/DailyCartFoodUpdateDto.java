package com.fitassist.backend.dto.request.food;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyCartFoodUpdateDto {

	@NotNull
	@Positive
	@Digits(integer = 4, fraction = 2)
	private BigDecimal quantity;

}

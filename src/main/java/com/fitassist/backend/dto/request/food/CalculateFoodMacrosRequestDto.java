package com.fitassist.backend.dto.request.food;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CalculateFoodMacrosRequestDto {

	@NotNull
	@Positive
	@Digits(integer = 4, fraction = 2)
	private BigDecimal quantity;

}

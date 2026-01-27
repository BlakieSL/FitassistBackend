package com.fitassist.backend.dto.request.food;

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
	private BigDecimal quantity;

}

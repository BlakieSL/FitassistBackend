package com.fitassist.backend.dto.request.activity;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateActivityCaloriesRequestDto {

	@NotNull
	@Positive
	@Max(1440)
	private Short time;

	@Positive
	@Digits(integer = 3, fraction = 1)
	@Max(500)
	private BigDecimal weight;

}

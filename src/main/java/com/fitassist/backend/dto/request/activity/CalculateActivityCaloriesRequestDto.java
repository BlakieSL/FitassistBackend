package com.fitassist.backend.dto.request.activity;

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
	private int time;

	@Positive
	private BigDecimal weight;

}

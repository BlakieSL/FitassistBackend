package com.fitassist.backend.dto.request.food;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyCartFoodCreateDto {

	@NotNull
	private BigDecimal quantity;

	@NotNull
	private LocalDate date;

}

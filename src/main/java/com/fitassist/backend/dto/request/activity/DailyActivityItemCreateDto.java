package com.fitassist.backend.dto.request.activity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class DailyActivityItemCreateDto {

	@NotNull
	@Positive
	private Short time;

	@Positive
	private BigDecimal weight;

	@NotNull
	private LocalDate date;

}

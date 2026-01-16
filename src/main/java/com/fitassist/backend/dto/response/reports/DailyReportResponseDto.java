package com.fitassist.backend.dto.response.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.pojo.FoodMacros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportResponseDto implements Serializable {

	private LocalDate date;

	private BigDecimal totalCaloriesConsumed;

	private BigDecimal totalCaloriesBurned;

	private BigDecimal netCalories;

	private FoodMacros macros;

}

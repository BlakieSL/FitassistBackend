package source.code.dto.response.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicReportResponseDto implements Serializable {

	private List<DailyReportResponseDto> dailyReports;

	private BigDecimal avgCaloriesConsumed;

	private BigDecimal avgCaloriesBurned;

	private BigDecimal avgNetCalories;

	private FoodMacros avgMacros;

}

package source.code.dto.response.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.pojo.ReportStats;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicReportResponseDto implements Serializable {

	private List<DailyReportResponseDto> dailyReports;

	private FoodMacros avgMacros;

	private ReportStats stats;

}

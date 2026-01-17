package com.fitassist.backend.dto.response.reports;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.pojo.ReportStats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

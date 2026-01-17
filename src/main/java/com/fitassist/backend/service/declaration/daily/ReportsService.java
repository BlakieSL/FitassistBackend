package com.fitassist.backend.service.declaration.daily;

import com.fitassist.backend.dto.response.reports.DailyReportResponseDto;
import com.fitassist.backend.dto.response.reports.PeriodicReportResponseDto;
import com.fitassist.backend.dto.response.reports.UserActionCountsDto;
import org.apache.coyote.BadRequestException;

import java.time.LocalDate;
import java.util.List;

public interface ReportsService {

	DailyReportResponseDto getDailyReport(LocalDate date);

	PeriodicReportResponseDto getPeriodicReport(LocalDate startDate, LocalDate endDate) throws BadRequestException;

	List<UserActionCountsDto> getHeatmap();

}

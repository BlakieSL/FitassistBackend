package source.code.service.declaration.daily;

import source.code.dto.response.reports.DailyReportResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportsService {

	DailyReportResponseDto getDailyReport(LocalDate date);

	List<DailyReportResponseDto> getPeriodicReport(LocalDate startDate, LocalDate endDate);

}

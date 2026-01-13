package source.code.service.declaration.daily;

import org.apache.coyote.BadRequestException;
import source.code.dto.response.reports.DailyReportResponseDto;
import source.code.dto.response.reports.PeriodicReportResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportsService {

	DailyReportResponseDto getDailyReport(LocalDate date);

	PeriodicReportResponseDto getPeriodicReport(LocalDate startDate, LocalDate endDate) throws BadRequestException;

}

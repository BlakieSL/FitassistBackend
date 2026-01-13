package source.code.controller.daily;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.reports.DailyReportResponseDto;
import source.code.service.declaration.daily.ReportsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/reports")
public class ReportsController {

	private final ReportsService reportsService;

	public ReportsController(ReportsService reportsService) {
		this.reportsService = reportsService;
	}

	@GetMapping("/daily/{date}")
	public ResponseEntity<DailyReportResponseDto> getDailyReport(@PathVariable LocalDate date) {
		DailyReportResponseDto report = reportsService.getDailyReport(date);
		return ResponseEntity.ok(report);
	}

	@GetMapping("/periodic/")
	public ResponseEntity<List<DailyReportResponseDto>> getPeriodicReport(@RequestParam LocalDate fromDate,
			@RequestParam LocalDate toDate) {
		List<DailyReportResponseDto> reports = reportsService.getPeriodicReport(fromDate, toDate);
		return ResponseEntity.ok(reports);
	}

}

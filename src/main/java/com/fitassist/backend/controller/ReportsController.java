package com.fitassist.backend.controller;

import com.fitassist.backend.dto.response.reports.DailyReportResponseDto;
import com.fitassist.backend.dto.response.reports.PeriodicReportResponseDto;
import com.fitassist.backend.dto.response.reports.UserActionCountsDto;
import com.fitassist.backend.service.declaration.daily.ReportsService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<PeriodicReportResponseDto> getPeriodicReport(@RequestParam LocalDate fromDate,
			@RequestParam LocalDate toDate) throws BadRequestException {
		PeriodicReportResponseDto report = reportsService.getPeriodicReport(fromDate, toDate);
		return ResponseEntity.ok(report);
	}

	@GetMapping("/heatmap")
	public ResponseEntity<List<UserActionCountsDto>> getHeatmap() {
		List<UserActionCountsDto> heatmap = reportsService.getHeatmap();
		return ResponseEntity.ok(heatmap);
	}

}

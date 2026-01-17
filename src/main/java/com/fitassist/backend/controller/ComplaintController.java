package com.fitassist.backend.controller;

import com.fitassist.backend.annotation.AdminOrModerator;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.service.declaration.complaint.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintController {

	private final ComplaintService complaintService;

	public ComplaintController(ComplaintService complaintService) {
		this.complaintService = complaintService;
	}

	@PostMapping
	public ResponseEntity<ComplaintResponseDto> createComplaint(
			@Valid @RequestBody ComplaintCreateDto complaintCreateDto) {
		ComplaintResponseDto response = complaintService.createComplaint(complaintCreateDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@AdminOrModerator
	@PutMapping("/{complaintId}/resolve")
	public ResponseEntity<Void> resolveComplaint(@PathVariable int complaintId) {
		complaintService.resolveComplaint(complaintId);
		return ResponseEntity.noContent().build();
	}

	@AdminOrModerator
	@GetMapping("/all")
	public ResponseEntity<Page<ComplaintResponseDto>> getAllComplaints(
			@PageableDefault(size = 100, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<ComplaintResponseDto> response = complaintService.getAllComplaints(pageable);
		return ResponseEntity.ok(response);
	}

	@AdminOrModerator
	@GetMapping("/{complaintId}")
	public ResponseEntity<ComplaintResponseDto> getComplaintById(@PathVariable int complaintId) {
		ComplaintResponseDto response = complaintService.getComplaintById(complaintId);
		return ResponseEntity.ok(response);
	}

}

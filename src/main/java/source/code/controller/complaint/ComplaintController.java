package source.code.controller.complaint;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.AdminOnly;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.dto.response.comment.ComplaintResponseDto;
import source.code.service.declaration.complaint.ComplaintService;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintController {
    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<Void> createComplaint(
            @Valid @RequestBody ComplaintCreateDto complaintCreateDto
    ) {
        complaintService.createComplaint(complaintCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @AdminOnly
    @PutMapping("/{complaintId}/resolve")
    public ResponseEntity<Void> resolveComplaint(@PathVariable int complaintId) {
        complaintService.resolveComplaint(complaintId);
        return ResponseEntity.noContent().build();
    }

    @AdminOnly
    @GetMapping("/all")
    public ResponseEntity<Page<ComplaintResponseDto>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Sort sortObj = Sort.by(
                Sort.Direction.fromString(sortParams[1]),
                sortParams[0]
        );

        Page<ComplaintResponseDto> response = complaintService
                .getAllComplaints(PageRequest.of(page, size, sortObj));

        return ResponseEntity.ok(response);
    }

    @AdminOnly
    @GetMapping("/{complaintId}")
    public ResponseEntity<ComplaintResponseDto> getComplaintById(@PathVariable int complaintId) {
        ComplaintResponseDto response = complaintService.getComplaintById(complaintId);
        return ResponseEntity.ok(response);
    }
}

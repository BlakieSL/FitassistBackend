package source.code.controller.complaint;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.helper.annotation.AdminOnly;
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
        return ResponseEntity.noContent().build();
    }

    @AdminOnly
    @PutMapping("/{complaintId}/resolve")
    public ResponseEntity<Void> resolveComplaint(@PathVariable int complaintId) {
        complaintService.resolveComplaint(complaintId);
        return ResponseEntity.noContent().build();
    }
}

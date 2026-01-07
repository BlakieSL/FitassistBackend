package source.code.service.declaration.complaint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.dto.response.comment.ComplaintResponseDto;

public interface ComplaintService {

	ComplaintResponseDto createComplaint(ComplaintCreateDto complaintCreateDto);

	void resolveComplaint(int complaintId);

	Page<ComplaintResponseDto> getAllComplaints(Pageable pageable);

	ComplaintResponseDto getComplaintById(int complaintId);

}

package com.fitassist.backend.service.declaration.complaint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;

public interface ComplaintService {

	ComplaintResponseDto createComplaint(ComplaintCreateDto complaintCreateDto);

	void resolveComplaint(int complaintId);

	Page<ComplaintResponseDto> getAllComplaints(Pageable pageable);

	ComplaintResponseDto getComplaintById(int complaintId);

}

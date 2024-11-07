package source.code.service.declaration.complaint;

import source.code.dto.request.complaint.ComplaintCreateDto;

public interface ComplaintService {
    void createComplaint(ComplaintCreateDto complaintCreateDto);
}

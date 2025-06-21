package source.code.service.implementation.complaint;

import org.springframework.stereotype.Service;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.complaint.ComplaintMapper;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ComplaintBase;
import source.code.model.complaint.ComplaintStatus;
import source.code.model.complaint.ThreadComplaint;
import source.code.repository.CommentComplaintRepository;
import source.code.repository.ThreadComplaintRepository;
import source.code.service.declaration.complaint.ComplaintService;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintMapper complaintMapper;
    private final CommentComplaintRepository commentComplaintRepository;
    private final ThreadComplaintRepository threadComplaintRepository;

    public ComplaintServiceImpl(ComplaintMapper complaintMapper,
                                CommentComplaintRepository commentComplaintRepository,
                                ThreadComplaintRepository threadComplaintRepository) {
        this.complaintMapper = complaintMapper;
        this.commentComplaintRepository = commentComplaintRepository;
        this.threadComplaintRepository = threadComplaintRepository;
    }

    @Override
    public void createComplaint(ComplaintCreateDto complaintCreateDto) {
        int userId = AuthorizationUtil.getUserId();
        switch (complaintCreateDto.getSubClass()){
            case COMMENT_COMPLAINT -> {
                CommentComplaint complaint = complaintMapper.toCommentComplaint(
                        complaintCreateDto,
                        userId
                );
                commentComplaintRepository.save(complaint);
            }
            case THREAD_COMPLAINT -> {
                ThreadComplaint complaint = complaintMapper.toThreadComplaint(
                        complaintCreateDto,
                        userId
                );
                threadComplaintRepository.save(complaint);
            }
        }
    }

    @Override
    public void resolveComplaint(int complaintId) {
        ComplaintBase complaint = commentComplaintRepository.findById(complaintId)
                .orElseThrow(() -> RecordNotFoundException.of(ComplaintBase.class, complaintId));
        complaint.setStatus(ComplaintStatus.RESOLVED);
    }
}

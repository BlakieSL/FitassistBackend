package source.code.service.implementation.complaint;

import org.springframework.stereotype.Service;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.mapper.complaint.ComplaintMapper;
import source.code.model.forum.CommentComplaint;
import source.code.model.forum.ComplaintBase;
import source.code.model.forum.ThreadComplaint;
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
        switch (complaintCreateDto.getSubClass()){
            case COMMENT_COMPLAINT -> {
                CommentComplaint complaint = complaintMapper.toCommentComplaint(complaintCreateDto);
                commentComplaintRepository.save(complaint);
            }
            case THREAD_COMPLAINT -> {
                ThreadComplaint complaint = complaintMapper.toThreadComplaint(complaintCreateDto);
                threadComplaintRepository.save(complaint);
            }
        }
    }
}

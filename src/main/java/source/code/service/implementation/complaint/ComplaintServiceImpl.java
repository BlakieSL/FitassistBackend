package source.code.service.implementation.complaint;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.dto.response.comment.ComplaintResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.complaint.ComplaintMapper;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ComplaintBase;
import source.code.model.complaint.ComplaintStatus;
import source.code.model.complaint.ThreadComplaint;
import source.code.repository.CommentComplaintRepository;
import source.code.repository.ComplaintRepository;
import source.code.repository.ThreadComplaintRepository;
import source.code.service.declaration.complaint.ComplaintService;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintMapper complaintMapper;
    private final CommentComplaintRepository commentComplaintRepository;
    private final ThreadComplaintRepository threadComplaintRepository;
    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImpl(ComplaintMapper complaintMapper,
                                CommentComplaintRepository commentComplaintRepository,
                                ThreadComplaintRepository threadComplaintRepository,
                                ComplaintRepository complaintRepository) {
        this.complaintMapper = complaintMapper;
        this.commentComplaintRepository = commentComplaintRepository;
        this.threadComplaintRepository = threadComplaintRepository;
        this.complaintRepository = complaintRepository;
    }

    @Transactional
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

    @Transactional
    @Override
    public void resolveComplaint(int complaintId) {
        ComplaintBase complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> RecordNotFoundException.of(ComplaintBase.class, complaintId));

        if (complaint instanceof CommentComplaint) {
            complaint.setStatus(ComplaintStatus.RESOLVED);
            commentComplaintRepository.save((CommentComplaint) complaint);
        } else if (complaint instanceof ThreadComplaint) {
            complaint.setStatus(ComplaintStatus.RESOLVED);
            threadComplaintRepository.save((ThreadComplaint) complaint);
        }
    }

    @Override
    public Page<ComplaintResponseDto> getAllComplaints(Pageable pageable) {
        Page<ComplaintBase> complaints = complaintRepository.findAll(pageable);
        return complaints.map(complaintMapper::toResponseDto);
    }

    @Override
    public ComplaintResponseDto getComplaintById(int complaintId) {
        ComplaintBase complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> RecordNotFoundException.of(ComplaintBase.class, complaintId));
        return complaintMapper.toResponseDto(complaint);
    }
}

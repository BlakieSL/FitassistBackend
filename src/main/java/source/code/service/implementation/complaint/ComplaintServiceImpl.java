package source.code.service.implementation.complaint;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.dto.response.comment.ComplaintResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.ComplaintMapper;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ComplaintBase;
import source.code.model.complaint.ComplaintStatus;
import source.code.model.complaint.ThreadComplaint;
import source.code.repository.ComplaintRepository;
import source.code.service.declaration.complaint.ComplaintService;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintMapper complaintMapper;
    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImpl(ComplaintMapper complaintMapper,
                                ComplaintRepository complaintRepository) {
        this.complaintMapper = complaintMapper;
        this.complaintRepository = complaintRepository;
    }

    @Transactional
    @Override
    public void createComplaint(ComplaintCreateDto complaintCreateDto) {
        int userId = AuthorizationUtil.getUserId();
        switch (complaintCreateDto.getSubClass()) {
            case COMMENT_COMPLAINT -> {
                CommentComplaint complaint = complaintMapper.toCommentComplaint(
                        complaintCreateDto,
                        userId
                );
                complaintRepository.save(complaint);
            }
            case THREAD_COMPLAINT -> {
                ThreadComplaint complaint = complaintMapper.toThreadComplaint(
                        complaintCreateDto,
                        userId
                );
                complaintRepository.save(complaint);
            }
        }
    }

    @Transactional
    @Override
    public void resolveComplaint(int complaintId) {
        ComplaintBase complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> RecordNotFoundException.of(ComplaintBase.class, complaintId));

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaintRepository.save(complaint);
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

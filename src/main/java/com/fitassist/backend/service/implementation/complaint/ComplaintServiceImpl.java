package com.fitassist.backend.service.implementation.complaint;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.exception.InvalidFilterValueException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.mapper.ComplaintMapper;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintBase;
import com.fitassist.backend.model.complaint.ComplaintStatus;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.repository.ComplaintRepository;
import com.fitassist.backend.service.declaration.complaint.ComplaintService;

@Service
public class ComplaintServiceImpl implements ComplaintService {

	private final ComplaintMapper complaintMapper;

	private final ComplaintRepository complaintRepository;

	public ComplaintServiceImpl(ComplaintMapper complaintMapper, ComplaintRepository complaintRepository) {
		this.complaintMapper = complaintMapper;
		this.complaintRepository = complaintRepository;
	}

	@Transactional
	@Override
	public ComplaintResponseDto createComplaint(ComplaintCreateDto complaintCreateDto) {
		int userId = AuthorizationUtil.getUserId();
		ComplaintBase savedComplaint;

		switch (complaintCreateDto.getSubClass()) {
			case COMMENT_COMPLAINT -> {
				CommentComplaint complaint = complaintMapper.toCommentComplaint(complaintCreateDto, userId);
				savedComplaint = complaintRepository.save(complaint);
			}
			case THREAD_COMPLAINT -> {
				ThreadComplaint complaint = complaintMapper.toThreadComplaint(complaintCreateDto, userId);
				savedComplaint = complaintRepository.save(complaint);
			}
			default -> throw new InvalidFilterValueException(
					"Unsupported complaint subclass: " + complaintCreateDto.getSubClass());
		}

		return getComplaintById(savedComplaint.getId());
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

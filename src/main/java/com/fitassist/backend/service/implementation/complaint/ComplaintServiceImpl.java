package com.fitassist.backend.service.implementation.complaint;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.exception.InvalidFilterValueException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.ComplaintMapper;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintBase;
import com.fitassist.backend.model.complaint.ComplaintStatus;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.repository.ComplaintRepository;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.complaint.ComplaintService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

	private final ComplaintMapper complaintMapper;

	private final ComplaintRepository complaintRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public ComplaintServiceImpl(ComplaintMapper complaintMapper, ComplaintRepository complaintRepository,
			MediaRepository mediaRepository, AwsS3Service s3Service) {
		this.complaintMapper = complaintMapper;
		this.complaintRepository = complaintRepository;
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
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
		return complaints.map(complaint -> {
			ComplaintResponseDto dto = complaintMapper.toResponseDto(complaint);
			populateImageUrls(dto, complaint);
			return dto;
		});
	}

	private void populateImageUrls(ComplaintResponseDto dto, ComplaintBase complaint) {
		MediaConnectedEntity mediaType = getMediaType(complaint);
		List<String> imageUrls = mediaRepository.findByParentIdAndParentType(complaint.getId(), mediaType)
			.stream()
			.map(media -> s3Service.getImage(media.getImageName()))
			.toList();
		dto.setImageUrls(imageUrls);
	}

	@Override
	public ComplaintResponseDto getComplaintById(int complaintId) {
		ComplaintBase complaint = complaintRepository.findById(complaintId)
			.orElseThrow(() -> RecordNotFoundException.of(ComplaintBase.class, complaintId));
		ComplaintResponseDto dto = complaintMapper.toResponseDto(complaint);
		populateImageUrls(dto, complaint);
		return dto;
	}

	private MediaConnectedEntity getMediaType(ComplaintBase complaint) {
		if (complaint instanceof CommentComplaint) {
			return MediaConnectedEntity.COMMENT_COMPLAINT;
		}
		else if (complaint instanceof ThreadComplaint) {
			return MediaConnectedEntity.THREAD_COMPLAINT;
		}
		throw new InvalidFilterValueException("Unsupported complaint subclass");
	}

}

package com.fitassist.backend.service.implementation.complaint;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.exception.InvalidFilterValueException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.complaint.ComplaintMapper;
import com.fitassist.backend.mapper.complaint.ComplaintMappingContext;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintBase;
import com.fitassist.backend.model.complaint.ComplaintStatus;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.*;
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

	private final CommentRepository commentRepository;

	private final ForumThreadRepository forumThreadRepository;

	private final UserRepository userRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public ComplaintServiceImpl(ComplaintMapper complaintMapper, ComplaintRepository complaintRepository,
			CommentRepository commentRepository, ForumThreadRepository forumThreadRepository,
			UserRepository userRepository, MediaRepository mediaRepository, AwsS3Service s3Service) {
		this.complaintMapper = complaintMapper;
		this.complaintRepository = complaintRepository;
		this.commentRepository = commentRepository;
		this.forumThreadRepository = forumThreadRepository;
		this.userRepository = userRepository;
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
	}

	@Transactional
	@Override
	public ComplaintResponseDto createComplaint(ComplaintCreateDto complaintCreateDto) {
		ComplaintBase savedComplaint;

		switch (complaintCreateDto.getSubClass()) {
			case COMMENT_COMPLAINT -> {
				ComplaintMappingContext context = prepareCommentComplaintContext(complaintCreateDto);
				CommentComplaint complaint = complaintMapper.toCommentComplaint(complaintCreateDto, context);
				savedComplaint = complaintRepository.save(complaint);
			}
			case THREAD_COMPLAINT -> {
				ComplaintMappingContext context = prepareThreadComplaintContext(complaintCreateDto);
				ThreadComplaint complaint = complaintMapper.toThreadComplaint(complaintCreateDto, context);
				savedComplaint = complaintRepository.save(complaint);
			}
			default -> throw new InvalidFilterValueException(
					"Unsupported complaint subclass: " + complaintCreateDto.getSubClass());
		}

		return getComplaintById(savedComplaint.getId());
	}

	private ComplaintMappingContext prepareCommentComplaintContext(ComplaintCreateDto createDto) {
		User user = findUser();
		Comment comment = findComment(createDto.getParentId());
		return ComplaintMappingContext.forCommentComplaint(user, comment);
	}

	private ComplaintMappingContext prepareThreadComplaintContext(ComplaintCreateDto createDto) {
		User user = findUser();
		ForumThread thread = findThread(createDto.getParentId());
		return ComplaintMappingContext.forThreadComplaint(user, thread);
	}

	private User findUser() {
		int userId = AuthorizationUtil.getUserId();
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	private Comment findComment(int commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> RecordNotFoundException.of(Comment.class, commentId));
	}

	private ForumThread findThread(int threadId) {
		return forumThreadRepository.findById(threadId)
			.orElseThrow(() -> RecordNotFoundException.of(ForumThread.class, threadId));
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

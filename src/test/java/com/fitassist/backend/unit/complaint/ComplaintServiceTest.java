package com.fitassist.backend.unit.complaint;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.request.complaint.ComplaintSubClass;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.complaint.ComplaintMapper;
import com.fitassist.backend.mapper.complaint.ComplaintMappingContext;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintStatus;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.*;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.implementation.complaint.ComplaintServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplaintServiceTest {

	private static final int USER_ID = 1;

	private static final int COMPLAINT_ID = 1;

	private static final int INVALID_COMPLAINT_ID = 999;

	private static final int PARENT_ID = 10;

	@Mock
	private ComplaintRepository complaintRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private ForumThreadRepository forumThreadRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ComplaintMapper complaintMapper;

	@Mock
	private MediaRepository mediaRepository;

	@Mock
	private AwsS3Service s3Service;

	@InjectMocks
	private ComplaintServiceImpl complaintService;

	private ComplaintCreateDto createDto;

	private CommentComplaint commentComplaint;

	private ThreadComplaint threadComplaint;

	private ComplaintResponseDto responseDto;

	private User user;

	private Comment comment;

	private ForumThread thread;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		createDto = new ComplaintCreateDto();
		createDto.setParentId(PARENT_ID);
		commentComplaint = new CommentComplaint();
		commentComplaint.setId(COMPLAINT_ID);
		threadComplaint = new ThreadComplaint();
		threadComplaint.setId(COMPLAINT_ID);
		responseDto = new ComplaintResponseDto();
		user = new User();
		comment = new Comment();
		thread = new ForumThread();
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createComplaint_shouldCreateCommentComplaint() {
		createDto.setSubClass(ComplaintSubClass.COMMENT_COMPLAINT);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(commentRepository.findById(PARENT_ID)).thenReturn(Optional.of(comment));
		when(complaintMapper.toCommentComplaint(eq(createDto), any(ComplaintMappingContext.class)))
			.thenReturn(commentComplaint);
		when(complaintRepository.save(commentComplaint)).thenReturn(commentComplaint);
		when(complaintRepository.findById(COMPLAINT_ID)).thenReturn(Optional.of(commentComplaint));
		when(complaintMapper.toResponse(commentComplaint)).thenReturn(responseDto);
		when(mediaRepository.findByParentIdAndParentType(COMPLAINT_ID, MediaConnectedEntity.COMMENT_COMPLAINT))
			.thenReturn(Collections.emptyList());

		ComplaintResponseDto result = complaintService.createComplaint(createDto);

		assertEquals(responseDto, result);
		verify(complaintRepository).save(commentComplaint);
		verify(complaintRepository).findById(COMPLAINT_ID);
		verify(complaintMapper).toResponse(commentComplaint);
	}

	@Test
	void createComplaint_shouldCreateThreadComplaint() {
		createDto.setSubClass(ComplaintSubClass.THREAD_COMPLAINT);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(PARENT_ID)).thenReturn(Optional.of(thread));
		when(complaintMapper.toThreadComplaint(eq(createDto), any(ComplaintMappingContext.class)))
			.thenReturn(threadComplaint);
		when(complaintRepository.save(threadComplaint)).thenReturn(threadComplaint);
		when(complaintRepository.findById(COMPLAINT_ID)).thenReturn(Optional.of(threadComplaint));
		when(complaintMapper.toResponse(threadComplaint)).thenReturn(responseDto);
		when(mediaRepository.findByParentIdAndParentType(COMPLAINT_ID, MediaConnectedEntity.THREAD_COMPLAINT))
			.thenReturn(Collections.emptyList());

		ComplaintResponseDto result = complaintService.createComplaint(createDto);

		assertEquals(responseDto, result);
		verify(complaintRepository).save(threadComplaint);
		verify(complaintRepository).findById(COMPLAINT_ID);
		verify(complaintMapper).toResponse(threadComplaint);
	}

	@Test
	void createComplaint_shouldThrowExceptionWhenUserNotFound() {
		createDto.setSubClass(ComplaintSubClass.COMMENT_COMPLAINT);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> complaintService.createComplaint(createDto));

		verifyNoInteractions(complaintMapper);
		verifyNoInteractions(complaintRepository);
	}

	@Test
	void createComplaint_shouldThrowExceptionWhenCommentNotFound() {
		createDto.setSubClass(ComplaintSubClass.COMMENT_COMPLAINT);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(commentRepository.findById(PARENT_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> complaintService.createComplaint(createDto));

		verifyNoInteractions(complaintMapper);
		verifyNoInteractions(complaintRepository);
	}

	@Test
	void createComplaint_shouldThrowExceptionWhenThreadNotFound() {
		createDto.setSubClass(ComplaintSubClass.THREAD_COMPLAINT);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(PARENT_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> complaintService.createComplaint(createDto));

		verifyNoInteractions(complaintMapper);
		verifyNoInteractions(complaintRepository);
	}

	@Test
	void resolveComplaint_shouldResolveCommentComplaint() {
		when(complaintRepository.findById(COMPLAINT_ID)).thenReturn(Optional.of(commentComplaint));

		complaintService.resolveComplaint(COMPLAINT_ID);

		assertEquals(ComplaintStatus.RESOLVED, commentComplaint.getStatus());
		verify(complaintRepository, times(1)).findById(any(Integer.class));
		verify(complaintRepository, times(1)).save(commentComplaint);
	}

	@Test
	void resolveComplaint_shouldThrowExceptionWhenComplaintNotFound() {
		when(complaintRepository.findById(INVALID_COMPLAINT_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> complaintService.resolveComplaint(INVALID_COMPLAINT_ID));
		verify(complaintRepository, times(1)).findById(INVALID_COMPLAINT_ID);
	}

}

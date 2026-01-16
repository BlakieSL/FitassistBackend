package com.fitassist.backend.unit.complaint;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.dto.request.complaint.ComplaintSubClass;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.mapper.ComplaintMapper;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintStatus;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import com.fitassist.backend.repository.ComplaintRepository;
import com.fitassist.backend.service.implementation.complaint.ComplaintServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplaintServiceTest {

	private static final int USER_ID = 1;

	private static final int COMPLAINT_ID = 1;

	private static final int INVALID_COMPLAINT_ID = 999;

	@Mock
	private ComplaintRepository complaintRepository;

	@Mock
	private ComplaintMapper complaintMapper;

	@InjectMocks
	private ComplaintServiceImpl complaintService;

	private ComplaintCreateDto createDto;

	private CommentComplaint commentComplaint;

	private ThreadComplaint threadComplaint;

	private ComplaintResponseDto responseDto;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		createDto = new ComplaintCreateDto();
		commentComplaint = new CommentComplaint();
		commentComplaint.setId(COMPLAINT_ID);
		threadComplaint = new ThreadComplaint();
		threadComplaint.setId(COMPLAINT_ID);
		responseDto = new ComplaintResponseDto();
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
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
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(complaintMapper.toCommentComplaint(createDto, USER_ID)).thenReturn(commentComplaint);
		when(complaintRepository.save(commentComplaint)).thenReturn(commentComplaint);
		when(complaintRepository.findById(COMPLAINT_ID)).thenReturn(Optional.of(commentComplaint));
		when(complaintMapper.toResponseDto(commentComplaint)).thenReturn(responseDto);

		ComplaintResponseDto result = complaintService.createComplaint(createDto);

		assertEquals(responseDto, result);
		verify(complaintRepository).save(commentComplaint);
		verify(complaintRepository).findById(COMPLAINT_ID);
		verify(complaintMapper).toResponseDto(commentComplaint);
	}

	@Test
	void createComplaint_shouldCreateThreadComplaint() {
		createDto.setSubClass(ComplaintSubClass.THREAD_COMPLAINT);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(complaintMapper.toThreadComplaint(createDto, USER_ID)).thenReturn(threadComplaint);
		when(complaintRepository.save(threadComplaint)).thenReturn(threadComplaint);
		when(complaintRepository.findById(COMPLAINT_ID)).thenReturn(Optional.of(threadComplaint));
		when(complaintMapper.toResponseDto(threadComplaint)).thenReturn(responseDto);

		ComplaintResponseDto result = complaintService.createComplaint(createDto);

		assertEquals(responseDto, result);
		verify(complaintRepository).save(threadComplaint);
		verify(complaintRepository).findById(COMPLAINT_ID);
		verify(complaintMapper).toResponseDto(threadComplaint);
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

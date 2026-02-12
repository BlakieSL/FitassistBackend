package com.fitassist.backend.unit.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.comment.CommentCreateDto;
import com.fitassist.backend.dto.request.comment.CommentUpdateDto;
import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.comment.CommentMapper;
import com.fitassist.backend.mapper.comment.CommentMappingContext;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.CommentRepository;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.comment.CommentPopulationService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.comment.CommentServiceImpl;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private ValidationService validationService;

	@Mock
	private CommentMapper commentMapper;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private ForumThreadRepository forumThreadRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CommentPopulationService commentPopulationService;

	@InjectMocks
	private CommentServiceImpl commentService;

	private Comment comment;

	private CommentCreateDto createDto;

	private CommentResponseDto responseDto;

	private JsonMergePatch patch;

	private CommentUpdateDto patchedDto;

	private User user;

	private ForumThread thread;

	private int commentId;

	private int userId;

	private int threadId;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		comment = new Comment();
		createDto = new CommentCreateDto();
		responseDto = new CommentResponseDto();
		patchedDto = new CommentUpdateDto();
		user = new User();
		thread = new ForumThread();
		commentId = 1;
		userId = 1;
		threadId = 1;
		createDto.setThreadId(threadId);
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createComment_shouldCreateComment() {
		comment.setId(commentId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(threadId)).thenReturn(Optional.of(thread));
		when(commentMapper.toEntity(eq(createDto), any(CommentMappingContext.class))).thenReturn(comment);
		when(commentRepository.save(comment)).thenReturn(comment);
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(commentMapper.toResponse(comment)).thenReturn(responseDto);

		CommentResponseDto result = commentService.createComment(createDto);

		assertEquals(responseDto, result);
		verify(commentPopulationService).populate(responseDto);
	}

	@Test
	void createComment_shouldThrowExceptionWhenUserNotFound() {
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> commentService.createComment(createDto));

		verifyNoInteractions(commentMapper);
		verifyNoInteractions(commentRepository);
	}

	@Test
	void createComment_shouldThrowExceptionWhenThreadNotFound() {
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(threadId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> commentService.createComment(createDto));

		verifyNoInteractions(commentMapper);
		verifyNoInteractions(commentRepository);
	}

	@Test
	void createComment_shouldThrowExceptionWhenParentCommentNotFound() {
		int parentCommentId = 99;
		createDto.setParentCommentId(parentCommentId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(forumThreadRepository.findById(threadId)).thenReturn(Optional.of(thread));
		when(commentRepository.findById(parentCommentId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> commentService.createComment(createDto));

		verifyNoInteractions(commentMapper);
	}

	@Test
	void updateComment_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(jsonPatchService.createFromPatch(patch, CommentUpdateDto.class)).thenReturn(patchedDto);

		commentService.updateComment(commentId, patch);

		verify(validationService).validate(patchedDto);
		verify(commentMapper).update(comment, patchedDto);
		verify(commentRepository).save(comment);
	}

	@Test
	void updateComment_shouldThrowExceptionWhenCommentNotFound() {
		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> commentService.updateComment(commentId, patch));

		verifyNoInteractions(commentMapper, jsonPatchService, validationService);
		verify(commentRepository, times(1)).findById(commentId);
	}

	@Test
	void updateComment_shouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(jsonPatchService.createFromPatch(patch, CommentUpdateDto.class)).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> commentService.updateComment(commentId, patch));

		verifyNoInteractions(validationService);
		verify(commentRepository, never()).save(comment);
	}

	@Test
	void updateComment_shouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(jsonPatchService.createFromPatch(patch, CommentUpdateDto.class)).thenReturn(patchedDto);

		doThrow(new RuntimeException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> commentService.updateComment(commentId, patch));

		verify(validationService).validate(patchedDto);
		verify(commentRepository, never()).save(any(Comment.class));
	}

	@Test
	void deleteComment_shouldDelete() {
		doNothing().when(commentRepository).deleteById(commentId);

		commentService.deleteComment(commentId);

		verify(commentRepository).deleteById(commentId);
	}

	@Test
	void getComment_shouldReturnCommentWhenFound() {
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(commentMapper.toResponse(comment)).thenReturn(responseDto);

		CommentResponseDto result = commentService.getComment(commentId);

		assertEquals(responseDto, result);
		verify(commentPopulationService).populate(responseDto);
	}

	@Test
	void getComment_shouldThrowExceptionWhenCommentNotFound() {
		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> commentService.getComment(commentId));

		verifyNoInteractions(commentMapper);
	}

	@Test
	void getTopCommentsForThread_shouldReturnTopComments() {
		Pageable pageable = PageRequest.of(0, 100);
		List<Comment> comments = List.of(comment);
		Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());
		when(commentRepository.findAllByThreadIdAndParentCommentNull(threadId, pageable)).thenReturn(commentPage);
		when(commentMapper.toResponse(comment)).thenReturn(responseDto);

		Page<CommentResponseDto> result = commentService.getTopCommentsForThread(threadId, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(responseDto, result.getContent().getFirst());
		verify(commentPopulationService).populateList(List.of(responseDto));
	}

}

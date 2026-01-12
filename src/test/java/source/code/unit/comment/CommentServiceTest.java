package source.code.unit.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.comment.CommentUpdateDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.CommentMapper;
import source.code.model.thread.Comment;
import source.code.repository.CommentRepository;
import source.code.service.declaration.comment.CommentPopulationService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.comment.CommentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	private RepositoryHelper repositoryHelper;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private CommentPopulationService commentPopulationService;

	@InjectMocks
	private CommentServiceImpl commentService;

	private Comment comment;

	private CommentCreateDto createDto;

	private CommentResponseDto responseDto;

	private JsonMergePatch patch;

	private CommentUpdateDto patchedDto;

	private int commentId;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		comment = new Comment();
		createDto = new CommentCreateDto();
		responseDto = new CommentResponseDto();
		patchedDto = new CommentUpdateDto();
		commentId = 1;
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createComment_shouldCreateComment() {
		int userId = 1;
		comment.setId(commentId);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(commentMapper.toEntity(createDto, userId)).thenReturn(comment);
		when(commentRepository.save(comment)).thenReturn(comment);
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

		CommentResponseDto result = commentService.createComment(createDto);

		assertEquals(responseDto, result);
		verify(commentPopulationService).populate(responseDto);
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
		doNothing().when(commentRepository).deleteCommentDirectly(commentId);

		commentService.deleteComment(commentId);

		verify(commentRepository).deleteCommentDirectly(commentId);
	}

	@Test
	void getComment_shouldReturnCommentWhenFound() {
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

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
		int threadId = 1;
		Pageable pageable = PageRequest.of(0, 100);
		List<Comment> comments = List.of(comment);
		Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());
		when(commentRepository.findAllByThreadIdAndParentCommentNull(threadId, pageable)).thenReturn(commentPage);
		when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

		Page<CommentResponseDto> result = commentService.getTopCommentsForThread(threadId, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(responseDto, result.getContent().get(0));
		verify(commentPopulationService).populateList(List.of(responseDto));
	}

}

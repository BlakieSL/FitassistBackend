package source.code.service.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.comment.CommentUpdateDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.model.forum.Comment;
import source.code.repository.CommentRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.comment.CommentServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(commentMapper.toEntity(createDto, userId)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.createComment(createDto);

        assertEquals(responseDto, result);
    }

    @Test
    void updateComment_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, CommentUpdateDto.class))
                .thenReturn(patchedDto);

        commentService.updateComment(commentId, patch);

        verify(validationService).validate(patchedDto);
        verify(commentMapper).update(comment, patchedDto);
        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_shouldThrowExceptionWhenCommentNotFound() {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId))
                .thenThrow(RecordNotFoundException.of(Comment.class, commentId));

        assertThrows(RecordNotFoundException.class, () -> commentService.updateComment(commentId, patch));

        verifyNoInteractions(commentMapper, jsonPatchService, validationService, commentRepository);
    }

    @Test
    void updateComment_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, CommentUpdateDto.class))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> commentService.updateComment(commentId, patch));

        verifyNoInteractions(validationService);
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void updateComment_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, CommentUpdateDto.class)).thenReturn(patchedDto);

        doThrow(new RuntimeException("Validation failed")).when(validationService).validate(patchedDto);

        assertThrows(RuntimeException.class, () -> commentService.updateComment(commentId, patch));

        verify(validationService).validate(patchedDto);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void deleteComment_shouldDelete() {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);

        commentService.deleteComment(commentId);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowExceptionWhenCommentNotFound() {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId))
                .thenThrow(RecordNotFoundException.of(Comment.class, commentId));

        assertThrows(RecordNotFoundException.class, () -> commentService.deleteComment(commentId));

        verify(commentRepository, never()).delete(comment);
    }

    @Test
    void getComment_shouldReturnCommentWhenFound() {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.getComment(commentId);

        assertEquals(responseDto, result);
    }

    @Test
    void getComment_shouldThrowExceptionWhenCommentNotFound() {
        when(repositoryHelper.find(commentRepository, Comment.class, commentId))
                .thenThrow(RecordNotFoundException.of(Comment.class, commentId));

        assertThrows(RecordNotFoundException.class, () -> commentService.getComment(commentId));

        verifyNoInteractions(commentMapper);
    }

    @Test
    void countCommentsForThread_shouldReturnCount() {
        int threadId = 1;
        long count = 5L;
        when(commentRepository.countAllByThreadId(threadId)).thenReturn(count);

        long result = commentService.countCommentsForThread(threadId);

        assertEquals(count, result);
    }

    @Test
    void getTopCommentsForThread_shouldReturnTopComments() {
        int threadId = 1;
        List<Comment> comments = List.of(comment);
        List<CommentResponseDto> responseDtos = List.of(responseDto);
        when(commentRepository.findAllByThreadIdAndParentCommentNull(threadId))
                .thenReturn(comments);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

        List<CommentResponseDto> result = commentService.getTopCommentsForThread(threadId);

        assertEquals(responseDtos, result);
    }

    @Test
    void getReplies_shouldReturnReplies() {
        int directReplyId =2;
        comment.setId(directReplyId);

        List<Comment> replies = List.of(comment);
        List<CommentResponseDto> responseDtos = List.of(responseDto);
        when(commentRepository.findAllByParentCommentId(commentId)).thenReturn(replies);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDto);

        List<CommentResponseDto> result = commentService.getReplies(commentId);

        assertEquals(responseDtos, result);
    }

    @Test
    void getReplies_shouldFilterOutDirectSelfReplies() {
        int commentId = 1;
        Comment comment = new Comment();
        comment.setId(commentId);

        Comment selfReply = new Comment();
        selfReply.setId(commentId);

        List<Comment> replies = List.of(selfReply);
        when(commentRepository.findAllByParentCommentId(commentId)).thenReturn(replies);

        List<CommentResponseDto> result = commentService.getReplies(commentId);

        assertTrue(result.isEmpty());
        verify(commentRepository).findAllByParentCommentId(commentId);
        verify(commentMapper, never()).toResponseDto(selfReply);
    }

    @Test
    void getReplies_shouldFilterOutFurtherSelfReplies() {
        int commentId = 1;
        int replyId = 2;

        Comment comment = new Comment();
        comment.setId(commentId);

        Comment directReply = new Comment();
        directReply.setId(replyId);

        Comment furtherSelfReply = new Comment();
        furtherSelfReply.setId(replyId);

        List<Comment> replies = List.of(directReply);
        when(commentRepository.findAllByParentCommentId(commentId)).thenReturn(replies);
        when(commentRepository.findAllByParentCommentId(replyId))
                .thenReturn(List.of(furtherSelfReply));

        CommentResponseDto directReplyDto = new CommentResponseDto();
        directReplyDto.setId(replyId);
        when(commentMapper.toResponseDto(directReply)).thenReturn(directReplyDto);

        List<CommentResponseDto> result = commentService.getReplies(commentId);

        assertTrue(result.get(0).getReplies().isEmpty());
        verify(commentRepository).findAllByParentCommentId(commentId);
        verify(commentRepository).findAllByParentCommentId(replyId);
        verify(commentMapper).toResponseDto(directReply);
        verify(commentMapper, never()).toResponseDto(furtherSelfReply);
    }
}
package source.code.service.declaration.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.response.comment.CommentResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentCreateDto createDto);
    void updateComment(int commentId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException, AccessDeniedException;
    void deleteComment(int commentId) throws AccessDeniedException;
    CommentResponseDto getComment(int commentId);
    List<CommentResponseDto> getCommentsByThread(int threadId);
    List<CommentResponseDto> getReplies(int commentId);
}

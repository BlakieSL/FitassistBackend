package source.code.service.declaration.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentCreateDto createDto);

    void updateComment(int commentId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException;

    void deleteComment(int commentId);

    CommentResponseDto getComment(int commentId);

    List<CommentResponseDto> getTopCommentsForThread(int threadId);

    List<CommentResponseDto> getReplies(int commentId);

    Page<CommentSummaryDto> getFilteredComments(FilterDto filter, Pageable pageable);
}

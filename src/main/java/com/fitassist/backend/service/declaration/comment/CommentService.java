package com.fitassist.backend.service.declaration.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.comment.CommentCreateDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.comment.CommentAncestryDto;
import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

	CommentResponseDto createComment(CommentCreateDto createDto);

	void updateComment(int commentId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deleteComment(int commentId);

	CommentResponseDto getComment(int commentId);

	Page<CommentResponseDto> getTopCommentsForThread(int threadId, Pageable pageable);

	List<CommentResponseDto> getReplies(int commentId);

	Page<CommentSummaryDto> getFilteredComments(FilterDto filter, Pageable pageable);

	CommentAncestryDto getCommentAncestry(int commentId);

}

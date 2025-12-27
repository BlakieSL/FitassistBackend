package source.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.CommentOwnerOrAdmin;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.comment.CommentAncestryDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.service.declaration.comment.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	public ResponseEntity<CommentResponseDto> createComment(@Valid @RequestBody CommentCreateDto createDto) {
		CommentResponseDto responseDto = commentService.createComment(createDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@CommentOwnerOrAdmin
	@PatchMapping("/{commentId}")
	public ResponseEntity<Void> updateComment(@PathVariable int commentId, @RequestBody JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		commentService.updateComment(commentId, patch);
		return ResponseEntity.noContent().build();
	}

	@CommentOwnerOrAdmin
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable int commentId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<CommentResponseDto> getComment(@PathVariable int commentId) {
		CommentResponseDto responseDto = commentService.getComment(commentId);
		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/top/{threadId}")
	public ResponseEntity<Page<CommentResponseDto>> getTopCommentsForThread(@PathVariable int threadId,
			@PageableDefault(size = Integer.MAX_VALUE, sort = "createdAt",
					direction = Sort.Direction.DESC) Pageable pageable) {
		Page<CommentResponseDto> responseDtos = commentService.getTopCommentsForThread(threadId, pageable);
		return ResponseEntity.ok(responseDtos);
	}

	@GetMapping("/replies/{commentId}")
	public ResponseEntity<List<CommentResponseDto>> getReplies(@PathVariable int commentId) {
		List<CommentResponseDto> responseDtos = commentService.getReplies(commentId);
		return ResponseEntity.ok(responseDtos);
	}

	@PostMapping("/filter")
	public ResponseEntity<Page<CommentSummaryDto>> getFilteredComments(@Valid @RequestBody FilterDto filterDto,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<CommentSummaryDto> comments = commentService.getFilteredComments(filterDto, pageable);
		return ResponseEntity.ok(comments);
	}

	@GetMapping("/ancestry/{commentId}")
	public ResponseEntity<CommentAncestryDto> getCommentAncestry(@PathVariable int commentId) {
		CommentAncestryDto ancestry = commentService.getCommentAncestry(commentId);
		return ResponseEntity.ok(ancestry);
	}

}

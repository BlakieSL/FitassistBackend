package source.code.controller.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.CommentOwnerOrAdmin;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.service.declaration.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestBody CommentCreateDto createDto
    ) {
        CommentResponseDto responseDto = commentService.createComment(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @CommentOwnerOrAdmin
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable int commentId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
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

    @GetMapping("/count/{threadId}")
    public ResponseEntity<Long> countCommentsForThread(@PathVariable int threadId) {
        long count = commentService.countCommentsForThread(threadId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/top/{threadId}")
    public ResponseEntity<List<CommentResponseDto>> getTopCommentsForThread(
            @PathVariable int threadId
    ) {
        List<CommentResponseDto> responseDtos = commentService.getTopCommentsForThread(threadId);
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/replies/{commentId}")
    public ResponseEntity<List<CommentResponseDto>> getReplies(@PathVariable int commentId) {
        List<CommentResponseDto> responseDtos = commentService.getReplies(commentId);
        return ResponseEntity.ok(responseDtos);
    }
}

package source.code.controller.forumThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.ThreadOwnerOrAdmin;
import source.code.dto.request.forumThread.ForumThreadCreateDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.service.declaration.forumThread.ForumThreadService;

import java.util.List;

@RestController
@RequestMapping("/api/threads")
public class ForumThreadController {
    private final ForumThreadService forumThreadService;

    public ForumThreadController(ForumThreadService forumThreadService) {
        this.forumThreadService = forumThreadService;
    }

    @PostMapping
    public ResponseEntity<ForumThreadResponseDto> createForumThread(
            @Valid @RequestBody ForumThreadCreateDto createDto
    ) {
        ForumThreadResponseDto responseDto = forumThreadService.createForumThread(createDto);
        return ResponseEntity.ok(responseDto);
    }

    @ThreadOwnerOrAdmin
    @PatchMapping("/{forumThreadId}")
    public ResponseEntity<Void> updateForumThread(
            @PathVariable int forumThreadId,
            @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        forumThreadService.updateForumThread(forumThreadId, patch);
        return ResponseEntity.ok().build();
    }

    @ThreadOwnerOrAdmin
    @DeleteMapping("/{forumThreadId}")
    public ResponseEntity<Void> deleteForumThread(@PathVariable int forumThreadId) {
        forumThreadService.deleteForumThread(forumThreadId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{forumThreadId}")
    public ResponseEntity<ForumThreadResponseDto> getForumThread(@PathVariable int forumThreadId) {
        ForumThreadResponseDto responseDto = forumThreadService.getForumThread(forumThreadId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ForumThreadResponseDto>> getAllForumThreads() {
        List<ForumThreadResponseDto> responseDto = forumThreadService.getAllForumThreads();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<ForumThreadResponseDto>> getForumThreadsByCategory(
            @PathVariable int categoryId
    ) {
        List<ForumThreadResponseDto> responseDto = forumThreadService
                .getForumThreadsByCategory(categoryId);
        return ResponseEntity.ok(responseDto);
    }
}

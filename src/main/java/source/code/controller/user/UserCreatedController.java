package source.code.controller.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.service.declaration.user.UserCreatedService;

@RestController
@RequestMapping("/api/user-created")
public class UserCreatedController {
    private final UserCreatedService userCreatedService;

    public UserCreatedController(UserCreatedService userCreatedService) {
        this.userCreatedService = userCreatedService;
    }

    @GetMapping("/plans/user/{userId}")
    public ResponseEntity<Page<PlanSummaryDto>> getUserPlans(
            @PathVariable int userId,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PlanSummaryDto> plans = userCreatedService.getCreatedPlans(userId, pageable);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/recipes/user/{userId}")
    public ResponseEntity<Page<RecipeSummaryDto>> getUserRecipes(
            @PathVariable int userId,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RecipeSummaryDto> recipes = userCreatedService.getCreatedRecipes(userId, pageable);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/comments/user/{userId}")
    public ResponseEntity<Page<CommentSummaryDto>> getUserComments(
            @PathVariable int userId,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentSummaryDto> comments = userCreatedService.getCreatedComments(userId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/threads/user/{userId}")
    public ResponseEntity<Page<ForumThreadSummaryDto>> getUserThreads(
            @PathVariable int userId,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ForumThreadSummaryDto> threads = userCreatedService.getCreatedThreads(userId, pageable);
        return ResponseEntity.ok(threads);
    }
}

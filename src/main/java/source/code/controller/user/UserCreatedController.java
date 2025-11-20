package source.code.controller.user;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;

@RestController
@RequestMapping("/api/user-created")
public class UserCreatedController {
    private final UserCreatedService userCreatedService;

    public UserCreatedController(UserCreatedService userCreatedService) {
        this.userCreatedService = userCreatedService;
    }

    @GetMapping("/plans/user/{userId}")
    public ResponseEntity<List<PlanSummaryDto>> getUserPlans(
            @PathVariable int userId,
            @RequestParam(defaultValue = "DESC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.fromString(sort);
        List<PlanSummaryDto> plans = userCreatedService.getCreatedPlans(userId, sortDirection);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/recipes/user/{userId}")
    public ResponseEntity<List<RecipeSummaryDto>> getUserRecipes(
            @PathVariable int userId,
            @RequestParam(defaultValue = "DESC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.fromString(sort);
        List<RecipeSummaryDto> recipes = userCreatedService.getCreatedRecipes(userId, sortDirection);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/comments/user/{userId}")
    public ResponseEntity<List<CommentSummaryDto>> getUserComments(
            @PathVariable int userId,
            @RequestParam(defaultValue = "DESC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.fromString(sort);
        List<CommentSummaryDto> comments = userCreatedService.getCreatedComments(userId, sortDirection);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/threads/user/{userId}")
    public ResponseEntity<List<ForumThreadSummaryDto>> getUserThreads(
            @PathVariable int userId,
            @RequestParam(defaultValue = "DESC") String sort) {
        Sort.Direction sortDirection = Sort.Direction.fromString(sort);
        List<ForumThreadSummaryDto> threads = userCreatedService.getCreatedThreads(userId, sortDirection);
        return ResponseEntity.ok(threads);
    }
}

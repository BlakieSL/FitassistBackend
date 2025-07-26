package source.code.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
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
    public ResponseEntity<List<PlanResponseDto>> getUserPlans(@PathVariable int userId) {
        List<PlanResponseDto> plans = userCreatedService.getCreatedPlans(userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/recipes/user/{userId}")
    public ResponseEntity<List<RecipeResponseDto>> getUserRecipes(@PathVariable int userId) {
        List<RecipeResponseDto> recipes = userCreatedService.getCreatedRecipes(userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/comments/user/{userId}")
    public ResponseEntity<List<CommentResponseDto>> getUserComments(@PathVariable int userId) {
        List<CommentResponseDto> comments = userCreatedService.getCreatedComments(userId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/threads/user/{userId}")
    public ResponseEntity<List<ForumThreadResponseDto>> getUserThreads(@PathVariable int userId) {
        List<ForumThreadResponseDto> threads = userCreatedService.getCreatedThreads(userId);
        return ResponseEntity.ok(threads);
    }
}

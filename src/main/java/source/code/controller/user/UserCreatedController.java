package source.code.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponseDto>> getUserPlans() {
        List<PlanResponseDto> plans = userCreatedService.getCreatedPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<RecipeResponseDto>> getUserRecipes() {
        List<RecipeResponseDto> recipes = userCreatedService.getCreatedRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDto>> getUserComments() {
        List<CommentResponseDto> comments = userCreatedService.getCreatedComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/threads")
    public ResponseEntity<List<ForumThreadResponseDto>> getUserThreads() {
        List<ForumThreadResponseDto> threads = userCreatedService.getCreatedThreads();
        return ResponseEntity.ok(threads);
    }
}

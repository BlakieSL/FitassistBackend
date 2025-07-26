package source.code.service.declaration.user;

import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;

import java.util.List;

public interface UserCreatedService {
    List<PlanResponseDto> getCreatedPlans(int userId);
    List<RecipeResponseDto> getCreatedRecipes(int userId);
    List<CommentResponseDto> getCreatedComments(int userId);
    List<ForumThreadResponseDto> getCreatedThreads(int userId);
}
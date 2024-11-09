package source.code.service.declaration.user;

import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;

import java.util.List;

public interface UserCreatedService {
    List<PlanResponseDto> getCreatedPlans();
    List<RecipeResponseDto> getCreatedRecipes();
    List<CommentResponseDto> getCreatedComments();
    List<ForumThreadResponseDto> getCreatedThreads();
}
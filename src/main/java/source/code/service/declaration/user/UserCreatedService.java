package source.code.service.declaration.user;

import source.code.dto.response.PlanResponseDto;
import source.code.dto.response.RecipeResponseDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.helper.Enum.model.CreatedEntityType;

import java.util.List;

public interface UserCreatedService {
    List<PlanResponseDto> getCreatedPlans();
    List<RecipeResponseDto> getCreatedRecipes();
    List<CommentResponseDto> getCreatedComments();
    List<ForumThreadResponseDto> getCreatedThreads();
}
package source.code.service.declaration.user;

import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

import java.util.List;

public interface UserCreatedService {
    List<PlanSummaryDto> getCreatedPlans(int userId, String sortDirection);
    List<RecipeSummaryDto> getCreatedRecipes(int userId, String sortDirection);
    List<CommentSummaryDto> getCreatedComments(int userId, String sortDirection);
    List<ForumThreadSummaryDto> getCreatedThreads(int userId, String sortDirection);
}
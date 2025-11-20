package source.code.service.declaration.user;

import org.springframework.data.domain.Sort;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

import java.util.List;

public interface UserCreatedService {
    List<PlanSummaryDto> getCreatedPlans(int userId, Sort.Direction sortDirection);
    List<RecipeSummaryDto> getCreatedRecipes(int userId, Sort.Direction sortDirection);
    List<CommentSummaryDto> getCreatedComments(int userId, Sort.Direction sortDirection);
    List<ForumThreadSummaryDto> getCreatedThreads(int userId, Sort.Direction sortDirection);
}
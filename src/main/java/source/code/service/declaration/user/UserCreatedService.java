package source.code.service.declaration.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;

public interface UserCreatedService {
    Page<PlanSummaryDto> getCreatedPlans(int userId, Pageable pageable);
    Page<RecipeSummaryDto> getCreatedRecipes(int userId, Pageable pageable);
    Page<CommentSummaryDto> getCreatedComments(int userId, Pageable pageable);
    Page<ForumThreadSummaryDto> getCreatedThreads(int userId, Pageable pageable);
}
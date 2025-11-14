package source.code.service.implementation.user;

import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.mapper.forumThread.ForumThreadMapper;
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final PlanMapper planMapper;
    private final RecipeMapper recipeMapper;
    private final CommentMapper commentMapper;
    private final ForumThreadMapper forumThreadMapper;
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;
    public UserCreatedServiceImpl(PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  CommentMapper commentMapper,
                                  ForumThreadMapper forumThreadMapper,
                                  PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository) {
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.commentMapper = commentMapper;
        this.forumThreadMapper = forumThreadMapper;
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
    }

    @Override
    public List<PlanSummaryDto> getCreatedPlans(int userId) {
        return planRepository.findSummaryByUserId(isOwnProfile(userId), userId);
    }

    @Override
    public List<RecipeSummaryDto> getCreatedRecipes(int userId) {
        return recipeRepository.findSummaryByUserId(isOwnProfile(userId), userId);
    }

    @Override
    public List<CommentSummaryDto> getCreatedComments(int userId) {
        return commentRepository.findSummaryByUserId(userId);
    }

    @Override
    public List<ForumThreadSummaryDto> getCreatedThreads(int userId) {
        return forumThreadRepository.findSummaryByUserId(userId);
    }

    private boolean isOwnProfile(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}

package source.code.service.implementation.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.comment.CommentPopulationService;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.declaration.recipe.RecipePopulationService;
import source.code.service.declaration.thread.ForumThreadPopulationService;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final PlanMapper planMapper;
    private final RecipeMapper recipeMapper;
    private final CommentMapper commentMapper;
    private final ForumThreadMapper forumThreadMapper;
    private final PlanPopulationService planPopulationService;
    private final RecipePopulationService recipePopulationService;
    private final CommentPopulationService commentPopulationService;
    private final ForumThreadPopulationService forumThreadPopulationService;

    public UserCreatedServiceImpl(PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  CommentMapper commentMapper,
                                  ForumThreadMapper forumThreadMapper,
                                  PlanPopulationService planPopulationService,
                                  RecipePopulationService recipePopulationService,
                                  CommentPopulationService commentPopulationService,
                                  ForumThreadPopulationService forumThreadPopulationService) {
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.commentMapper = commentMapper;
        this.forumThreadMapper = forumThreadMapper;
        this.planPopulationService = planPopulationService;
        this.recipePopulationService = recipePopulationService;
        this.commentPopulationService = commentPopulationService;
        this.forumThreadPopulationService = forumThreadPopulationService;
    }

    @Override
    public Page<PlanSummaryDto> getCreatedPlans(int userId, Pageable pageable) {
        Page<Plan> planPage = planRepository.findCreatedByUserWithDetails(userId, isOwnProfile(userId), pageable);

        List<PlanSummaryDto> summaries = planPage.getContent().stream()
                .map(planMapper::toSummaryDto)
                .toList();

        planPopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, planPage.getTotalElements());
    }

    @Override
    public Page<RecipeSummaryDto> getCreatedRecipes(int userId, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findCreatedByUserWithDetails(userId, isOwnProfile(userId), pageable);

        List<RecipeSummaryDto> summaries = recipePage.getContent().stream()
                .map(recipeMapper::toSummaryDto)
                .toList();

        recipePopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, recipePage.getTotalElements());
    }

    @Override
    public Page<CommentSummaryDto> getCreatedComments(int userId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findCreatedByUserWithDetails(userId, pageable);

        List<CommentSummaryDto> summaries = commentPage.getContent().stream()
                .map(commentMapper::toSummaryDto)
                .toList();

        commentPopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, commentPage.getTotalElements());
    }

    @Override
    public Page<ForumThreadSummaryDto> getCreatedThreads(int userId, Pageable pageable) {
        Page<ForumThread> threadPage = forumThreadRepository.findCreatedByUserWithDetails(userId, pageable);

        List<ForumThreadSummaryDto> summaries = threadPage.getContent().stream()
                .map(forumThreadMapper::toSummaryDto)
                .toList();

        forumThreadPopulationService.populate(summaries);

        return new PageImpl<>(summaries, pageable, threadPage.getTotalElements());
    }

    private boolean isOwnProfile(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}

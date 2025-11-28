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
import source.code.mapper.plan.PlanMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.repository.*;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.plan.PlanPopulationService;
import source.code.service.declaration.recipe.RecipePopulationService;
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
    private final PlanPopulationService planPopulationService;
    private final RecipePopulationService recipePopulationService;
    private final ImageUrlPopulationService imagePopulationService;

    public UserCreatedServiceImpl(PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  PlanMapper planMapper,
                                  RecipeMapper recipeMapper,
                                  PlanPopulationService planPopulationService,
                                  RecipePopulationService recipePopulationService,
                                  ImageUrlPopulationService imagePopulationService) {
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.planMapper = planMapper;
        this.recipeMapper = recipeMapper;
        this.planPopulationService = planPopulationService;
        this.recipePopulationService = recipePopulationService;
        this.imagePopulationService = imagePopulationService;
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
        Page<CommentSummaryDto> commentPage = commentRepository.findCommentSummaryUnified(userId, null, false, pageable);

        commentPage.getContent().forEach(dto -> imagePopulationService.populateAuthorImage(dto,
                CommentSummaryDto::getAuthorImageName, CommentSummaryDto::setAuthorImageUrl));

        return commentPage;
    }

    @Override
    public Page<ForumThreadSummaryDto> getCreatedThreads(int userId, Pageable pageable) {
        Page<ForumThreadSummaryDto> threadPage = forumThreadRepository.findThreadSummaryUnified(userId, false, pageable);

        threadPage.getContent().forEach(dto -> imagePopulationService.populateAuthorImage(dto,
                ForumThreadSummaryDto::getAuthorImageName, ForumThreadSummaryDto::setAuthorImageUrl));

        return threadPage;
    }

    private boolean isOwnProfile(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}

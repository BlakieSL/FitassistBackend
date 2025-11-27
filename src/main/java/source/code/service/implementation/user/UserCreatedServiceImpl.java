package source.code.service.implementation.user;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.repository.*;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.helpers.RecipePopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.declaration.user.UserCreatedService;

import java.util.List;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final RecipeMapper recipeMapper;
    private final RecipePopulationService recipePopulationService;
    private final ImageUrlPopulationService imagePopulationService;
    private final SortingService sortingService;

    public UserCreatedServiceImpl(PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  RecipeMapper recipeMapper,
                                  RecipePopulationService recipePopulationService,
                                  ImageUrlPopulationService imagePopulationService,
                                  SortingService sortingService) {
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.recipeMapper = recipeMapper;
        this.recipePopulationService = recipePopulationService;
        this.imagePopulationService = imagePopulationService;
        this.sortingService = sortingService;
    }

    @Override
    public List<PlanSummaryDto> getCreatedPlans(int userId, Sort.Direction sortDirection) {
        return planRepository.findPlanSummaryUnified(userId, null, false, isOwnProfile(userId))
                .stream()
                .peek(dto -> imagePopulationService.populateAuthorAndEntityImages(dto,
                        PlanSummaryDto::getAuthorImageName, PlanSummaryDto::setAuthorImageUrl,
                        PlanSummaryDto::getFirstImageName, PlanSummaryDto::setFirstImageUrl))
                .sorted(sortingService.comparator(PlanSummaryDto::getCreatedAt, sortDirection))
                .toList();
    }

    @Override
    public List<RecipeSummaryDto> getCreatedRecipes(int userId, Sort.Direction sortDirection) {
        var summaries =  recipeRepository.findCreatedByUserWithDetails(userId, isOwnProfile(userId)).stream()
                .map(recipeMapper::toSummaryDto)
                .sorted(sortingService.comparator(RecipeSummaryDto::getCreatedAt, sortDirection))
                .toList();

        recipePopulationService.populate(summaries);
        return summaries;
    }

    @Override
    public List<CommentSummaryDto> getCreatedComments(int userId, Sort.Direction sortDirection) {
        return commentRepository.findCommentSummaryUnified(userId, null, false)
                .stream()
                .peek(dto -> imagePopulationService.populateAuthorImage(dto,
                        CommentSummaryDto::getAuthorImageName, CommentSummaryDto::setAuthorImageUrl))
                .sorted(sortingService.comparator(CommentSummaryDto::getCreatedAt, sortDirection))
                .toList();
    }

    @Override
    public List<ForumThreadSummaryDto> getCreatedThreads(int userId, Sort.Direction sortDirection) {
        return forumThreadRepository.findThreadSummaryUnified(userId, false)
                .stream()
                .peek(dto -> imagePopulationService.populateAuthorImage(dto,
                        ForumThreadSummaryDto::getAuthorImageName, ForumThreadSummaryDto::setAuthorImageUrl))
                .sorted(sortingService.comparator(ForumThreadSummaryDto::getCreatedAt, sortDirection))
                .toList();
    }

    private boolean isOwnProfile(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }
}

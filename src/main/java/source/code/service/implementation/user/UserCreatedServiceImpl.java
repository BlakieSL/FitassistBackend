package source.code.service.implementation.user;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.*;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.declaration.user.UserCreatedService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;
    private final ImageUrlPopulationService imagePopulationService;
    private final SortingService sortingService;

    public UserCreatedServiceImpl(PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  RecipeCategoryAssociationRepository recipeCategoryAssociationRepository,
                                  ImageUrlPopulationService imagePopulationService,
                                  SortingService sortingService) {
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.recipeCategoryAssociationRepository = recipeCategoryAssociationRepository;
        this.imagePopulationService = imagePopulationService;
        this.sortingService = sortingService;
    }

    @Override
    public List<PlanSummaryDto> getCreatedPlans(int userId, Sort.Direction sortDirection) {
        List<PlanSummaryDto> plans = new ArrayList<>(planRepository.findPlanSummaryUnified(userId, null, false, isOwnProfile(userId)));
        imagePopulationService.populateAuthorAndEntityImagesForList(plans,
                PlanSummaryDto::getAuthorImageName, PlanSummaryDto::setAuthorImageUrl,
                PlanSummaryDto::getFirstImageName, PlanSummaryDto::setFirstImageUrl);
        sortingService.sortByTimestamp(plans, PlanSummaryDto::getCreatedAt, sortDirection);
        return plans;
    }

    @Override
    public List<RecipeSummaryDto> getCreatedRecipes(int userId, Sort.Direction sortDirection) {
        List<RecipeSummaryDto> recipes = new ArrayList<>(recipeRepository.findRecipeSummaryUnified(userId, null, false, isOwnProfile(userId)));
        if (!recipes.isEmpty()) {
            var recipeIds = recipes.stream().map(RecipeSummaryDto::getId).toList();
            var categoriesMap = fetchCategoriesForRecipes(recipeIds);
            recipes.forEach(recipe -> recipe.setCategories(categoriesMap.getOrDefault(recipe.getId(), new ArrayList<>())));
        }
        imagePopulationService.populateAuthorAndEntityImagesForList(recipes,
                RecipeSummaryDto::getAuthorImageName, RecipeSummaryDto::setAuthorImageUrl,
                RecipeSummaryDto::getFirstImageName, RecipeSummaryDto::setFirstImageUrl);
        sortingService.sortByTimestamp(recipes, RecipeSummaryDto::getCreatedAt, sortDirection);
        return recipes;
    }

    @Override
    public List<CommentSummaryDto> getCreatedComments(int userId, Sort.Direction sortDirection) {
        List<CommentSummaryDto> comments = new ArrayList<>(commentRepository.findCommentSummaryUnified(userId, null, false));
        imagePopulationService.populateAuthorImageForList(comments,
                CommentSummaryDto::getAuthorImageName, CommentSummaryDto::setAuthorImageUrl);
        sortingService.sortByTimestamp(comments, CommentSummaryDto::getDateCreated, sortDirection);
        return comments;
    }

    @Override
    public List<ForumThreadSummaryDto> getCreatedThreads(int userId, Sort.Direction sortDirection) {
        List<ForumThreadSummaryDto> threads = new ArrayList<>(forumThreadRepository.findThreadSummaryUnified(userId, false));
        imagePopulationService.populateAuthorImageForList(threads,
                ForumThreadSummaryDto::getAuthorImageName, ForumThreadSummaryDto::setAuthorImageUrl);
        sortingService.sortByTimestamp(threads, ForumThreadSummaryDto::getDateCreated, sortDirection);
        return threads;
    }

    private boolean isOwnProfile(int userId) {
        return getUserId() == userId;
    }

    private int getUserId() {
        return AuthorizationUtil.getUserId();
    }

    private Map<Integer, List<RecipeCategoryShortDto>> fetchCategoriesForRecipes(List<Integer> recipeIds) {
        return recipeCategoryAssociationRepository.findCategoryDataByRecipeIds(recipeIds).stream()
                .collect(Collectors.groupingBy(
                        arr -> (Integer) arr[0],
                        Collectors.mapping(
                                arr -> new RecipeCategoryShortDto((Integer) arr[1], (String) arr[2]),
                                Collectors.toList()
                        )
                ));
    }
}

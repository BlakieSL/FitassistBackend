package source.code.service.implementation.user;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.CommentRepository;
import source.code.repository.ForumThreadRepository;
import source.code.repository.PlanRepository;
import source.code.repository.RecipeCategoryAssociationRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.UserCreatedService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UserCreatedServiceImpl implements UserCreatedService {
    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final AwsS3Service s3Service;
    private final RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;

    public UserCreatedServiceImpl(PlanRepository planRepository,
                                  RecipeRepository recipeRepository,
                                  CommentRepository commentRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  AwsS3Service s3Service,
                                  RecipeCategoryAssociationRepository recipeCategoryAssociationRepository) {
        this.planRepository = planRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.s3Service = s3Service;
        this.recipeCategoryAssociationRepository = recipeCategoryAssociationRepository;
    }

    @Override
    public List<PlanSummaryDto> getCreatedPlans(int userId) {
        List<PlanSummaryDto> plans = planRepository.findSummaryByUserId(isOwnProfile(userId), userId);
        populatePlanImageUrls(plans);
        return plans;
    }

    @Override
    public List<RecipeSummaryDto> getCreatedRecipes(int userId) {
        List<RecipeSummaryDto> recipes = recipeRepository.findSummaryByUserId(isOwnProfile(userId), userId);
        if (!recipes.isEmpty()) {
            List<Integer> recipeIds = recipes.stream().map(RecipeSummaryDto::getId).toList();
            Map<Integer, List<RecipeCategoryShortDto>> categoriesMap = fetchCategoriesForRecipes(recipeIds);
            recipes.forEach(recipe -> recipe.setCategories(categoriesMap.getOrDefault(recipe.getId(), new ArrayList<>())));
        }
        populateRecipeImageUrls(recipes);
        return recipes;
    }

    @Override
    public List<CommentSummaryDto> getCreatedComments(int userId) {
        List<CommentSummaryDto> comments = commentRepository.findSummaryByUserId(userId);
        populateCommentImageUrls(comments);
        return comments;
    }

    @Override
    public List<ForumThreadSummaryDto> getCreatedThreads(int userId) {
        List<ForumThreadSummaryDto> threads = forumThreadRepository.findSummaryByUserId(userId);
        populateThreadImageUrls(threads);
        return threads;
    }

    private void populatePlanImageUrls(List<PlanSummaryDto> plans) {
        plans.forEach(plan -> {
            populateImageUrl(plan.getAuthorImageUrl(), plan::setAuthorImageUrl);
            populateImageUrl(plan.getImageName(), plan::setFirstImageUrl);
        });
    }

    private void populateRecipeImageUrls(List<RecipeSummaryDto> recipes) {
        recipes.forEach(recipe -> {
            populateImageUrl(recipe.getAuthorImageUrl(), recipe::setAuthorImageUrl);
            populateImageUrl(recipe.getImageName(), recipe::setFirstImageUrl);
        });
    }

    private void populateCommentImageUrls(List<CommentSummaryDto> comments) {
        comments.forEach(comment ->
            populateImageUrl(comment.getAuthorImageUrl(), comment::setAuthorImageUrl)
        );
    }

    private void populateThreadImageUrls(List<ForumThreadSummaryDto> threads) {
        threads.forEach(thread ->
            populateImageUrl(thread.getAuthorImageUrl(), thread::setAuthorImageUrl)
        );
    }

    private void populateImageUrl(String imageName, Consumer<String> setter) {
        if (imageName != null) {
            setter.accept(s3Service.getImage(imageName));
        }
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

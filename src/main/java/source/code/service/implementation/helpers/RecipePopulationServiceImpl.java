package source.code.service.implementation.helpers;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.RecipeCountsProjection;
import source.code.dto.pojo.projection.RecipeIngredientCountProjection;
import source.code.dto.pojo.projection.RecipeInteractionDateProjection;
import source.code.dto.pojo.projection.RecipeUserInteractionProjection;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.model.media.Media;
import source.code.model.user.TypeOfInteraction;
import source.code.repository.MediaRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.UserRecipeRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.helpers.RecipePopulationService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecipePopulationServiceImpl implements RecipePopulationService {
    private final UserRecipeRepository userRecipeRepository;
    private final RecipeFoodRepository recipeFoodRepository;
    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;

    public RecipePopulationServiceImpl(
            UserRecipeRepository userRecipeRepository,
            RecipeFoodRepository recipeFoodRepository,
            MediaRepository mediaRepository,
            AwsS3Service s3Service) {
        this.userRecipeRepository = userRecipeRepository;
        this.recipeFoodRepository = recipeFoodRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void populate(List<RecipeSummaryDto> recipes) {
        if (recipes.isEmpty()) return;

        List<Integer> recipeIds = recipes.stream().map(RecipeSummaryDto::getId).toList();

        populateAuthorImages(recipes);
        populateImageUrls(recipes);
        populateCounts(recipes, recipeIds);
        populateIngredientsCount(recipes, recipeIds);
    }

    @Override
    public void populate(RecipeResponseDto recipe) {
        int userId = AuthorizationUtil.getUserId();

        populateAuthorImage(recipe);
        populateImageUrls(recipe);
        populateUserInteractionsAndCounts(recipe, userId);
    }

    @Override
    public void populateInteractionDates(List<RecipeSummaryDto> recipes, int userId, TypeOfInteraction type) {
        if (recipes.isEmpty()) return;

        List<Integer> recipeIds = recipes.stream()
                .map(RecipeSummaryDto::getId)
                .toList();

        Map<Integer, RecipeInteractionDateProjection> interactionDatesMap = userRecipeRepository
                .findInteractionDatesByRecipeIds(userId, type, recipeIds)
                .stream()
                .collect(Collectors.toMap(
                        RecipeInteractionDateProjection::getRecipeId,
                        projection -> projection
                ));

        recipes.forEach(recipe -> {
            RecipeInteractionDateProjection projection = interactionDatesMap.get(recipe.getId());
            if (projection != null) {
                recipe.setUserRecipeInteractionCreatedAt(projection.getCreatedAt());
            }
        });
    }

    private void populateAuthorImages(List<RecipeSummaryDto> recipes) {
        List<Integer> authorIds = recipes.stream()
                .map(RecipeSummaryDto::getAuthorId)
                .toList();

        if (authorIds.isEmpty()) return;

        Map<Integer, String> authorImageMap = mediaRepository
                .findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
                .stream()
                .collect(Collectors.toMap(Media::getParentId, Media::getImageName));

        recipes.forEach(recipe -> {
            String imageName = authorImageMap.get(recipe.getAuthorId());
            if (imageName != null) {
                recipe.setAuthorImageName(imageName);
                recipe.setAuthorImageUrl(s3Service.getImage(imageName));
            }
        });
    }

    private void populateImageUrls(List<RecipeSummaryDto> recipes) {
        recipes.forEach(recipe -> {
            if (recipe.getFirstImageName() != null) {
                recipe.setFirstImageUrl(s3Service.getImage(recipe.getFirstImageName()));
            }
        });
    }

    private void populateCounts(List<RecipeSummaryDto> recipes, List<Integer> recipeIds) {
        Map<Integer, RecipeCountsProjection> countsMap = userRecipeRepository
                .findCountsByRecipeIds(recipeIds)
                .stream()
                .collect(Collectors.toMap(
                        RecipeCountsProjection::getRecipeId,
                        projection -> projection
                ));

        recipes.forEach(recipe -> {
            RecipeCountsProjection counts = countsMap.get(recipe.getId());
            if (counts != null) {
                recipe.setLikesCount(counts.getLikesCount());
                recipe.setDislikesCount(counts.getDislikesCount());
                recipe.setSavesCount(counts.getSavesCount());
            }
        });
    }

    private void populateIngredientsCount(List<RecipeSummaryDto> recipes, List<Integer> recipeIds) {
        Map<Integer, Long> ingredientsMap = recipeFoodRepository
                .countByRecipeIds(recipeIds)
                .stream()
                .collect(Collectors.toMap(
                        RecipeIngredientCountProjection::getRecipeId,
                        RecipeIngredientCountProjection::getIngredientCount
                ));

        recipes.forEach(recipe ->
                recipe.setIngredientsCount(ingredientsMap.getOrDefault(recipe.getId(), 0L))
        );
    }

    private void populateAuthorImage(RecipeResponseDto recipe) {
        if (recipe.getAuthorId() == null) return;

        mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(recipe.getAuthorId(), MediaConnectedEntity.USER)
            .ifPresent(media -> {
                recipe.setAuthorImageName(media.getImageName());
                recipe.setAuthorImageUrl(s3Service.getImage(media.getImageName()));
            });
    }

    private void populateImageUrls(RecipeResponseDto recipe) {
            List<String> imageUrls = Objects.requireNonNull(recipe.getImageNames()).stream()
                    .map(s3Service::getImage)
                    .toList();
            recipe.setImageUrls(imageUrls);
    }

    private void populateUserInteractionsAndCounts(RecipeResponseDto recipe, int requestingUserId) {
        RecipeUserInteractionProjection result = userRecipeRepository
                .findUserInteractionsAndCounts(requestingUserId, recipe.getId());

        if (result == null) return;

        recipe.setLiked(result.isLiked());
        recipe.setDisliked(result.isDisliked());
        recipe.setSaved(result.isSaved());
        recipe.setLikesCount(result.likesCount());
        recipe.setDislikesCount(result.dislikesCount());
        recipe.setSavesCount(result.savesCount());
    }
}

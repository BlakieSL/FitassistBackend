package source.code.service.implementation.recipe;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.EntityCountsProjection;
import source.code.dto.pojo.projection.recipe.RecipeIngredientCountProjection;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.utils.AuthorizationUtil;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.UserRecipeRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.recipe.RecipePopulationService;

import java.util.List;
import java.util.Map;
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

        fetchAndPopulateAuthorImages(recipes);
        populateImageUrls(recipes);
        fetchAndPopulateUserInteractionsAndCounts(recipes, recipeIds);
        fetchAndPopulateIngredientsCount(recipes, recipeIds);
    }

    @Override
    public void populate(RecipeResponseDto recipe) {
        int userId = AuthorizationUtil.getUserId();

        fetchAndPopulateAuthorImage(recipe);
        fetchAndPopulateImageUrls(recipe);
        fetchAndPopulateUserInteractionsAndCounts(recipe, userId);
    }

    private void fetchAndPopulateAuthorImages(List<RecipeSummaryDto> recipes) {
        List<Integer> authorIds = recipes.stream()
                .map(recipe -> recipe.getAuthor().getId())
                .toList();

        if (authorIds.isEmpty()) return;

        Map<Integer, String> authorImageMap = mediaRepository
                .findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
                .stream()
                .collect(Collectors.toMap(Media::getParentId, Media::getImageName));

        recipes.forEach(recipe -> {
            String imageName = authorImageMap.get(recipe.getAuthor().getId());
            if (imageName != null) {
                recipe.getAuthor().setImageName(imageName);
                recipe.getAuthor().setImageUrl(s3Service.getImage(imageName));
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

    private void fetchAndPopulateUserInteractionsAndCounts(List<RecipeSummaryDto> recipes, List<Integer> recipeIds) {
        int userId = AuthorizationUtil.getUserId();

        Map<Integer, EntityCountsProjection> countsMap = userRecipeRepository
                .findCountsAndInteractionsByRecipeIds(userId, recipeIds)
                .stream()
                .collect(Collectors.toMap(
                        EntityCountsProjection::getEntityId,
                        projection -> projection
                ));

        recipes.forEach(recipe -> {
            EntityCountsProjection counts = countsMap.get(recipe.getId());
            if (counts != null) {
                recipe.setLikesCount(counts.likesCount());
                recipe.setDislikesCount(counts.dislikesCount());
                recipe.setSavesCount(counts.savesCount());
                recipe.setLiked(counts.isLiked());
                recipe.setDisliked(counts.isDisliked());
                recipe.setSaved(counts.isSaved());
            }
        });
    }

    private void fetchAndPopulateIngredientsCount(List<RecipeSummaryDto> recipes, List<Integer> recipeIds) {
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

    private void fetchAndPopulateAuthorImage(RecipeResponseDto recipe) {
        mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(recipe.getAuthor().getId(), MediaConnectedEntity.USER)
                .ifPresent(media -> {
                    recipe.getAuthor().setImageName(media.getImageName());
                    recipe.getAuthor().setImageUrl(s3Service.getImage(media.getImageName()));
                });
    }

    private void fetchAndPopulateImageUrls(RecipeResponseDto recipe) {
        List<String> imageUrls = mediaRepository.findByParentIdAndParentType(recipe.getId(), MediaConnectedEntity.RECIPE)
                .stream()
                .map(media -> s3Service.getImage(media.getImageName()))
                .toList();
        recipe.setImageUrls(imageUrls);
    }

    private void fetchAndPopulateUserInteractionsAndCounts(RecipeResponseDto recipe, int requestingUserId) {
        EntityCountsProjection result = userRecipeRepository
                .findCountsAndInteractionsByRecipeId(requestingUserId, recipe.getId());

        if (result == null) return;

        recipe.setLiked(result.isLiked());
        recipe.setDisliked(result.isDisliked());
        recipe.setSaved(result.isSaved());
        recipe.setLikesCount(result.likesCount());
        recipe.setDislikesCount(result.dislikesCount());
        recipe.setSavesCount(result.savesCount());
    }
}

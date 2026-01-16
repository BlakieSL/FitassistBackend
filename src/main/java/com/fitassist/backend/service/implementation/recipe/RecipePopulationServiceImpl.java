package com.fitassist.backend.service.implementation.recipe;

import org.springframework.stereotype.Service;
import com.fitassist.backend.dto.pojo.projection.EntityCountsProjection;
import com.fitassist.backend.dto.pojo.projection.recipe.RecipeIngredientCountProjection;
import com.fitassist.backend.dto.response.recipe.RecipeResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.repository.RecipeFoodRepository;
import com.fitassist.backend.repository.UserRecipeRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.recipe.RecipePopulationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipePopulationServiceImpl implements RecipePopulationService {

	private final UserRecipeRepository userRecipeRepository;

	private final RecipeFoodRepository recipeFoodRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public RecipePopulationServiceImpl(UserRecipeRepository userRecipeRepository,
			RecipeFoodRepository recipeFoodRepository, MediaRepository mediaRepository, AwsS3Service s3Service) {
		this.userRecipeRepository = userRecipeRepository;
		this.recipeFoodRepository = recipeFoodRepository;
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
	}

	@Override
	public void populate(List<RecipeSummaryDto> recipes) {
		if (recipes.isEmpty())
			return;

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
		fetchAndPopulateIngredientImages(recipe);
	}

	private void fetchAndPopulateAuthorImages(List<RecipeSummaryDto> recipes) {
		List<Integer> authorIds = recipes.stream().map(recipe -> recipe.getAuthor().getId()).toList();

		if (authorIds.isEmpty())
			return;

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
			.collect(Collectors.toMap(EntityCountsProjection::getEntityId, projection -> projection));

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
		Map<Integer, Long> ingredientsMap = recipeFoodRepository.countByRecipeIds(recipeIds)
			.stream()
			.collect(Collectors.toMap(RecipeIngredientCountProjection::getRecipeId,
					RecipeIngredientCountProjection::getIngredientCount));

		recipes.forEach(recipe -> recipe.setIngredientsCount(ingredientsMap.getOrDefault(recipe.getId(), 0L)));
	}

	private void fetchAndPopulateAuthorImage(RecipeResponseDto recipe) {
		mediaRepository
			.findFirstByParentIdAndParentTypeOrderByIdAsc(recipe.getAuthor().getId(), MediaConnectedEntity.USER)
			.ifPresent(media -> {
				recipe.getAuthor().setImageName(media.getImageName());
				recipe.getAuthor().setImageUrl(s3Service.getImage(media.getImageName()));
			});
	}

	private void fetchAndPopulateImageUrls(RecipeResponseDto recipe) {
		List<String> imageUrls = mediaRepository
			.findByParentIdAndParentType(recipe.getId(), MediaConnectedEntity.RECIPE)
			.stream()
			.map(media -> s3Service.getImage(media.getImageName()))
			.toList();
		recipe.setImageUrls(imageUrls);
	}

	private void fetchAndPopulateUserInteractionsAndCounts(RecipeResponseDto recipe, int requestingUserId) {
		EntityCountsProjection result = userRecipeRepository.findCountsAndInteractionsByRecipeId(requestingUserId,
				recipe.getId());

		if (result == null)
			return;

		recipe.setLiked(result.isLiked());
		recipe.setDisliked(result.isDisliked());
		recipe.setSaved(result.isSaved());
		recipe.setLikesCount(result.likesCount());
		recipe.setDislikesCount(result.dislikesCount());
		recipe.setSavesCount(result.savesCount());
	}

	private void fetchAndPopulateIngredientImages(RecipeResponseDto recipe) {
		if (recipe.getFoods().isEmpty())
			return;

		List<Integer> foodIds = recipe.getFoods().stream().map(food -> food.getIngredient().getId()).toList();

		Map<Integer, String> foodImageMap = mediaRepository
			.findFirstMediaByParentIds(foodIds, MediaConnectedEntity.FOOD)
			.stream()
			.collect(Collectors.toMap(Media::getParentId, Media::getImageName));

		recipe.getFoods().forEach(food -> {
			String imageName = foodImageMap.get(food.getIngredient().getId());
			if (imageName != null) {
				food.getIngredient().setFirstImageUrl(s3Service.getImage(imageName));
			}
		});
	}

}

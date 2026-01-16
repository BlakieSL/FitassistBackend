package com.fitassist.backend.service.implementation.user.interaction.withType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fitassist.backend.dto.response.recipe.RecipeResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.exception.NotSupportedInteractionTypeException;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.mapper.recipe.RecipeMapper;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.user.TypeOfInteraction;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserRecipe;
import com.fitassist.backend.repository.RecipeRepository;
import com.fitassist.backend.repository.UserRecipeRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.recipe.RecipePopulationService;
import com.fitassist.backend.service.declaration.user.SavedService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userRecipeService")
public class UserRecipeServiceImpl extends GenericSavedService<Recipe, UserRecipe, RecipeResponseDto>
		implements SavedService {

	private final RecipeMapper recipeMapper;

	private final RecipePopulationService recipePopulationService;

	public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository, RecipeRepository recipeRepository,
			UserRepository userRepository, RecipeMapper recipeMapper, RecipePopulationService recipePopulationService) {
		super(userRepository, recipeRepository, userRecipeRepository, Recipe.class, UserRecipe.class);
		this.recipeMapper = recipeMapper;
		this.recipePopulationService = recipePopulationService;
	}

	@Override
	@CacheEvict(value = CacheNames.RECIPES, key = "#entityId")
	public void saveToUser(int entityId, TypeOfInteraction type) {
		super.saveToUser(entityId, type);
	}

	@Override
	@CacheEvict(value = CacheNames.RECIPES, key = "#entityId")
	public void deleteFromUser(int entityId, TypeOfInteraction type) {
		super.deleteFromUser(entityId, type);
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, TypeOfInteraction type, Pageable pageable) {
		Page<UserRecipe> userRecipePage = ((UserRecipeRepository) userEntityRepository).findAllByUserIdAndType(userId,
				type, pageable);

		List<Integer> recipeIds = userRecipePage.getContent().stream().map(ur -> ur.getRecipe().getId()).toList();

		List<Recipe> recipesWithDetails = ((RecipeRepository) entityRepository).findByIdsWithDetails(recipeIds);

		Map<Integer, Recipe> recipeMap = recipesWithDetails.stream().collect(Collectors.toMap(Recipe::getId, r -> r));

		List<RecipeSummaryDto> summaries = userRecipePage.getContent().stream().map(ur -> {
			Recipe recipe = recipeMap.get(ur.getRecipe().getId());
			RecipeSummaryDto dto = recipeMapper.toSummaryDto(recipe);
			dto.setInteractionCreatedAt(ur.getCreatedAt());
			return dto;
		}).toList();

		recipePopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userRecipePage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type) {
		return ((UserRecipeRepository) userEntityRepository).existsByUserIdAndRecipeIdAndType(userId, entityId, type);
	}

	@Override
	protected UserRecipe createUserEntity(User user, Recipe entity, TypeOfInteraction type) {
		if (!entity.getIsPublic()) {
			throw new NotSupportedInteractionTypeException("Cannot save private recipe");
		}
		return UserRecipe.createWithUserRecipeType(user, entity, type);
	}

	@Override
	protected Optional<UserRecipe> findUserEntityOptional(int userId, int entityId, TypeOfInteraction type) {
		return ((UserRecipeRepository) userEntityRepository).findByUserIdAndRecipeIdAndType(userId, entityId, type);
	}

}

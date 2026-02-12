package com.fitassist.backend.unit.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.dto.request.recipe.RecipeFoodCreateDto;
import com.fitassist.backend.dto.request.recipe.RecipeFoodUpdateDto;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.food.FoodMapper;
import com.fitassist.backend.mapper.recipe.RecipeFoodMapper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.recipe.RecipeFood;
import com.fitassist.backend.repository.FoodRepository;
import com.fitassist.backend.repository.RecipeFoodRepository;
import com.fitassist.backend.repository.RecipeRepository;
import com.fitassist.backend.service.declaration.food.FoodPopulationService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.recipe.RecipeFoodServiceImpl;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeFoodServiceTest {

	@Mock
	private ValidationService validationService;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private FoodMapper foodMapper;

	@Mock
	private RecipeFoodMapper recipeFoodMapper;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private RecipeFoodRepository recipeFoodRepository;

	@Mock
	private FoodRepository foodRepository;

	@Mock
	private RecipeRepository recipeRepository;

	@Mock
	private FoodPopulationService foodPopulationService;

	@InjectMocks
	private RecipeFoodServiceImpl recipeFoodService;

	private Recipe recipe;

	private Food food;

	private RecipeFood recipeFood;

	private RecipeFoodCreateDto createDto;

	private JsonMergePatch patch;

	private FoodSummaryDto foodSummaryDto;

	private int recipeId;

	private int foodId;

	@BeforeEach
	void setUp() {
		recipeId = 1;
		foodId = 1;

		recipe = new Recipe();
		food = mock(Food.class);
		recipeFood = RecipeFood.of(BigDecimal.valueOf(100), recipe, food);
		createDto = new RecipeFoodCreateDto(
				List.of(new RecipeFoodCreateDto.FoodQuantityPair(foodId, BigDecimal.valueOf(100))));
		patch = mock(JsonMergePatch.class);
		foodSummaryDto = new FoodSummaryDto();
	}

	@Test
	void saveFoodToRecipe_shouldSaveFoodToRecipe() {
		when(food.getId()).thenReturn(foodId);
		when(recipeFoodRepository.findByRecipeIdAndFoodIds(recipeId, List.of(foodId))).thenReturn(List.of());
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
		when(foodRepository.findAllById(List.of(foodId))).thenReturn(List.of(food));

		recipeFoodService.saveFoodToRecipe(recipeId, createDto);

		verify(recipeFoodRepository).saveAll(anyList());
	}

	@Test
	void saveFoodToRecipe_shouldThrowExceptionWhenFoodAlreadyAdded() {
		when(food.getId()).thenReturn(foodId);
		when(recipeFoodRepository.findByRecipeIdAndFoodIds(recipeId, List.of(foodId))).thenReturn(List.of(recipeFood));

		assertThrows(NotUniqueRecordException.class, () -> recipeFoodService.saveFoodToRecipe(recipeId, createDto));

		verify(recipeFoodRepository, never()).saveAll(anyList());
	}

	@Test
	void saveFoodToRecipe_shouldThrowExceptionWhenRecipeNotFound() {
		when(recipeFoodRepository.findByRecipeIdAndFoodIds(recipeId, List.of(foodId))).thenReturn(List.of());
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId))
			.thenThrow(RecordNotFoundException.of(Recipe.class, recipeId));

		assertThrows(RecordNotFoundException.class, () -> recipeFoodService.saveFoodToRecipe(recipeId, createDto));

		verify(recipeFoodRepository, never()).saveAll(anyList());
	}

	@Test
	void saveFoodToRecipe_shouldThrowExceptionWhenFoodNotFound() {
		when(recipeFoodRepository.findByRecipeIdAndFoodIds(recipeId, List.of(foodId))).thenReturn(List.of());
		when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
		when(foodRepository.findAllById(List.of(foodId))).thenReturn(List.of());

		assertThrows(RecordNotFoundException.class, () -> recipeFoodService.saveFoodToRecipe(recipeId, createDto));

		verify(recipeFoodRepository, never()).saveAll(anyList());
	}

	@Test
	void updateFoodRecipe_shouldUpdateFoodRecipe() throws JsonPatchException, JsonProcessingException {
		RecipeFoodUpdateDto updateDto = new RecipeFoodUpdateDto(BigDecimal.valueOf(300));
		when(recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(Optional.of(recipeFood));
		doReturn(updateDto).when(jsonPatchService).createFromPatch(eq(patch), eq(RecipeFoodUpdateDto.class));

		recipeFoodService.updateFoodRecipe(recipeId, foodId, patch);

		verify(validationService).validate(updateDto);
		verify(recipeFoodMapper).update(recipeFood, updateDto);
		verify(recipeFoodRepository).save(recipeFood);
	}

	@Test
	void updateFoodRecipe_shouldThrowExceptionWhenRecipeFoodNotFound() {
		when(recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> recipeFoodService.updateFoodRecipe(recipeId, foodId, patch));

		verify(recipeFoodRepository, never()).save(any(RecipeFood.class));
	}

	@Test
	void deleteFoodFromRecipe_shouldDeleteFoodFromRecipe() {
		when(recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(Optional.of(recipeFood));

		recipeFoodService.deleteFoodFromRecipe(foodId, recipeId);

		verify(recipeFoodRepository).delete(recipeFood);
	}

	@Test
	void deleteFoodFromRecipe_shouldThrowExceptionWhenRecipeFoodNotFound() {
		when(recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> recipeFoodService.deleteFoodFromRecipe(foodId, recipeId));

		verify(recipeFoodRepository, never()).delete(any(RecipeFood.class));
	}

	@Test
	void getFoodsByRecipe_shouldReturnFoodsByRecipe() {
		when(recipeFoodRepository.findByRecipeId(recipeId)).thenReturn(List.of(recipeFood));
		when(foodMapper.toSummary(food)).thenReturn(foodSummaryDto);

		List<FoodSummaryDto> result = recipeFoodService.getFoodsByRecipe(recipeId);

		assertEquals(1, result.size());
		assertSame(foodSummaryDto, result.getFirst());
		verify(foodPopulationService).populate(result);
	}

	@Test
	void getFoodsByRecipe_shouldReturnEmptyListWhenNoFoods() {
		when(recipeFoodRepository.findByRecipeId(recipeId)).thenReturn(List.of());

		List<FoodSummaryDto> result = recipeFoodService.getFoodsByRecipe(recipeId);

		assertTrue(result.isEmpty());
		verify(foodPopulationService).populate(result);
	}

}

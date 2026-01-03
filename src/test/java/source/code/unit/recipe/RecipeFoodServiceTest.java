package source.code.unit.recipe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.recipe.FilterRecipesByFoodsDto;
import source.code.dto.request.recipe.RecipeFoodCreateDto;
import source.code.dto.request.recipe.RecipeFoodUpdateDto;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.FoodMapper;
import source.code.mapper.recipe.RecipeFoodMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.food.Food;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeFood;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.food.FoodPopulationService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipeService;
import source.code.service.implementation.recipe.RecipeFoodServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RecipeFoodServiceTest {

	@Mock
	private RecipeService recipeService;

	@Mock
	private ValidationService validationService;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private FoodMapper foodMapper;

	@Mock
	private RecipeMapper recipeMapper;

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
		when(foodMapper.toSummaryDto(food)).thenReturn(foodSummaryDto);

		List<FoodSummaryDto> result = recipeFoodService.getFoodsByRecipe(recipeId);

		assertEquals(1, result.size());
		assertSame(foodSummaryDto, result.get(0));
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

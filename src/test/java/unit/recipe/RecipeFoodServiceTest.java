package unit.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.recipe.FilterRecipesByFoodsDto;
import source.code.dto.request.recipe.RecipeFoodCreateDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.food.FoodMapper;
import source.code.mapper.recipe.RecipeFoodMapper;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.food.Food;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeFood;
import source.code.repository.FoodRepository;
import source.code.repository.RecipeFoodRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipeService;
import source.code.service.implementation.recipe.RecipeFoodServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private RecipeFoodServiceImpl recipeFoodService;

    private Recipe recipe;
    private Food food;
    private RecipeFood recipeFood;
    private RecipeFoodCreateDto createDto;
    private JsonMergePatch patch;
    private FoodResponseDto foodResponseDto;
    private int recipeId;
    private int foodId;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        food = new Food();
        recipeFood = RecipeFood.of(100, recipe, food);
        createDto = new RecipeFoodCreateDto(100);
        patch = mock(JsonMergePatch.class);
        foodResponseDto = new FoodResponseDto();
        recipeId = 1;
        foodId = 1;
    }

    @Test
    void saveFoodToRecipe_shouldSaveFoodToRecipe() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(recipeFoodRepository.existsByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(false);

        recipeFoodService.saveFoodToRecipe(recipeId, foodId, createDto);

        verify(recipeFoodRepository).save(any(RecipeFood.class));
    }

    @Test
    void saveFoodToRecipe_shouldThrowExceptionWhenFoodAlreadyAdded() {
        when(recipeFoodRepository.existsByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(true);

        assertThrows(NotUniqueRecordException.class, () ->
                recipeFoodService.saveFoodToRecipe(recipeId, foodId, createDto)
        );

        verify(recipeFoodRepository, never()).save(any(RecipeFood.class));
    }

    @Test
    void saveFoodToRecipe_shouldThrowExceptionWhenRecipeNotFound() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId))
                .thenThrow(RecordNotFoundException.of(Recipe.class, recipeId));

        assertThrows(RecordNotFoundException.class, () ->
                recipeFoodService.saveFoodToRecipe(recipeId, foodId, createDto)
        );

        verify(recipeFoodRepository, never()).save(any(RecipeFood.class));
    }

    @Test
    void saveFoodToRecipe_shouldThrowExceptionWhenFoodNotFound() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(repositoryHelper.find(foodRepository, Food.class, foodId))
                .thenThrow(RecordNotFoundException.of(Food.class, foodId));

        assertThrows(RecordNotFoundException.class, () ->
                recipeFoodService.saveFoodToRecipe(recipeId, foodId, createDto)
        );

        verify(recipeFoodRepository, never()).save(any(RecipeFood.class));
    }

    @Test
    void updateFoodRecipe_shouldUpdateFoodRecipe() throws JsonPatchException, JsonProcessingException {
        when(recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(Optional.of(recipeFood));
        doReturn(createDto).when(jsonPatchService).applyPatch(
                eq(patch),
                any(RecipeFoodCreateDto.class),
                eq(RecipeFoodCreateDto.class)
        );

        recipeFoodService.updateFoodRecipe(recipeId, foodId, patch);

        verify(validationService).validate(createDto);
        verify(recipeFoodMapper).update(recipeFood, createDto);
        verify(recipeFoodRepository).save(recipeFood);
    }

    @Test
    void updateFoodRecipe_shouldThrowExceptionWhenRecipeFoodNotFound() {
        when(recipeFoodRepository.findByRecipeIdAndFoodId(recipeId, foodId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                recipeFoodService.updateFoodRecipe(recipeId, foodId, patch)
        );

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

        assertThrows(RecordNotFoundException.class, () ->
                recipeFoodService.deleteFoodFromRecipe(foodId, recipeId)
        );

        verify(recipeFoodRepository, never()).delete(any(RecipeFood.class));
    }

    @Test
    void getFoodsByRecipe_shouldReturnFoodsByRecipe() {
        when(recipeFoodRepository.findByRecipeId(recipeId)).thenReturn(List.of(recipeFood));
        when(foodMapper.toResponseDto(food)).thenReturn(foodResponseDto);

        List<FoodResponseDto> result = recipeFoodService.getFoodsByRecipe(recipeId);

        assertEquals(1, result.size());
        assertSame(foodResponseDto, result.get(0));
    }

    @Test
    void getFoodsByRecipe_shouldReturnEmptyListWhenNoFoods() {
        when(recipeFoodRepository.findByRecipeId(recipeId)).thenReturn(List.of());

        List<FoodResponseDto> result = recipeFoodService.getFoodsByRecipe(recipeId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getRecipesByFoods_shouldReturnRecipesByFoods() {
        List<Integer> foodIds = List.of(foodId);
        FilterRecipesByFoodsDto filter = FilterRecipesByFoodsDto.of(foodIds);
        List<RecipeResponseDto> recipeResponseDtos = List.of(new RecipeResponseDto());

        when(recipeService.getFilteredRecipes(any())).thenReturn(recipeResponseDtos);

        List<RecipeResponseDto> result = recipeFoodService.getRecipesByFoods(filter);

        assertEquals(1, result.size());
        assertSame(recipeResponseDtos.get(0), result.get(0));
    }

    @Test
    void getRecipesByFoods_shouldReturnEmptyListWhenNoRecipes() {
        List<Integer> foodIds = List.of(foodId);
        FilterRecipesByFoodsDto filter = FilterRecipesByFoodsDto.of(foodIds);

        when(recipeService.getFilteredRecipes(any())).thenReturn(List.of());

        List<RecipeResponseDto> result = recipeFoodService.getRecipesByFoods(filter);

        assertTrue(result.isEmpty());
    }
}
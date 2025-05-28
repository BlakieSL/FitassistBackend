package unit.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.request.recipe.RecipeUpdateDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.event.events.Recipe.RecipeCreateEvent;
import source.code.event.events.Recipe.RecipeDeleteEvent;
import source.code.event.events.Recipe.RecipeUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.recipe.RecipeCategoryAssociation;
import source.code.repository.RecipeCategoryAssociationRepository;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.recipe.RecipeServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private RecipeCategoryAssociationRepository recipeCategoryAssociationRepository;
    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ValidationService validationService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private RecipeServiceImpl recipeService;

    private Recipe recipe;
    private RecipeCreateDto createDto;
    private RecipeResponseDto responseDto;
    private JsonMergePatch patch;
    private RecipeUpdateDto patchedDto;
    private int recipeId;
    private int userId;
    private FilterDto filter;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        createDto = new RecipeCreateDto();
        responseDto = new RecipeResponseDto();
        patchedDto = new RecipeUpdateDto();
        recipeId = 1;
        userId = 1;
        filter = new FilterDto();
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void createRecipe_shouldCreateRecipe() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(recipeMapper.toEntity(createDto, userId)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        RecipeResponseDto result = recipeService.createRecipe(createDto);

        assertEquals(responseDto, result);
    }

    @Test
    void createRecipe_shouldPublishEvent() {
        ArgumentCaptor<RecipeCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(RecipeCreateEvent.class);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(recipeMapper.toEntity(createDto, userId)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        recipeService.createRecipe(createDto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(recipe, eventCaptor.getValue().getRecipe());
    }

    @Test
    void updateRecipe_shouldUpdateRecipe() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, RecipeUpdateDto.class)).thenReturn(patchedDto);
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        recipeService.updateRecipe(recipeId, patch);

        verify(validationService).validate(patchedDto);
        verify(recipeMapper).updateRecipe(recipe, patchedDto);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void updateRecipe_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
        ArgumentCaptor<RecipeUpdateEvent> eventCaptor = ArgumentCaptor.forClass(RecipeUpdateEvent.class);
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, RecipeUpdateDto.class)).thenReturn(patchedDto);
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        recipeService.updateRecipe(recipeId, patch);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(recipe, eventCaptor.getValue().getRecipe());
    }

    @Test
    void updateRecipe_shouldThrowExceptionWhenRecipeNotFound() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId))
                .thenThrow(RecordNotFoundException.of(Recipe.class, recipeId));

        assertThrows(RecordNotFoundException.class, () -> recipeService.updateRecipe(recipeId, patch));

        verifyNoInteractions(recipeMapper, jsonPatchService, validationService, eventPublisher);
        verify(recipeRepository, never()).save(recipe);
    }

    @Test
    void updateRecipe_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, RecipeUpdateDto.class))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> recipeService.updateRecipe(recipeId, patch));

        verifyNoInteractions(validationService, eventPublisher);
        verify(recipeRepository, never()).save(recipe);
    }

    @Test
    void updateRecipe_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, RecipeUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () -> recipeService.updateRecipe(recipeId, patch));

        verify(validationService).validate(patchedDto);
        verifyNoInteractions(eventPublisher);
        verify(recipeRepository, never()).save(recipe);
    }

    @Test
    void deleteRecipe_shouldDeleteRecipe() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);

        recipeService.deleteRecipe(recipeId);

        verify(recipeRepository).delete(recipe);
        verify(eventPublisher).publishEvent(any(RecipeDeleteEvent.class));
    }

    @Test
    void deleteRecipe_shouldThrowExceptionWhenRecipeNotFound() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId))
                .thenThrow(RecordNotFoundException.of(Recipe.class, recipeId));

        assertThrows(RecordNotFoundException.class, () -> recipeService.deleteRecipe(recipeId));

        verify(recipeRepository, never()).delete(recipe);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void getRecipe_shouldReturnRecipeWhenFound() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        RecipeResponseDto result = recipeService.getRecipe(recipeId);

        assertEquals(responseDto, result);
    }

    @Test
    void getRecipe_shouldThrowExceptionWhenRecipeNotFound() {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId))
                .thenThrow(RecordNotFoundException.of(Recipe.class, recipeId));

        assertThrows(RecordNotFoundException.class, () -> recipeService.getRecipe(recipeId));

        verifyNoInteractions(recipeMapper);
    }

    @Test
    void getAllRecipes_shouldReturnAllRecipes() {
        List<RecipeResponseDto> responseDtos = List.of(responseDto);

        when(repositoryHelper.findAll(eq(recipeRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<RecipeResponseDto> result = recipeService.getAllRecipes();

        assertEquals(responseDtos, result);
        verify(repositoryHelper).findAll(eq(recipeRepository), any(Function.class));
    }

    @Test
    void getAllRecipes_shouldReturnEmptyListWhenNoRecipes() {
        List<RecipeResponseDto> responseDtos = List.of();
        when(repositoryHelper.findAll(eq(recipeRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<RecipeResponseDto> result = recipeService.getAllRecipes();

        assertTrue(result.isEmpty());
        verify(repositoryHelper).findAll(eq(recipeRepository), any(Function.class));
    }

    @Test
    void getFilteredRecipes_shouldReturnFilteredRecipes() {
        when(recipeRepository.findAll(any(Specification.class))).thenReturn(List.of(recipe));
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        List<RecipeResponseDto> result = recipeService.getFilteredRecipes(filter);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
        verify(recipeRepository).findAll(any(Specification.class));
        verify(recipeMapper).toResponseDto(recipe);
    }

    @Test
    void getFilteredRecipes_shouldReturnEmptyListWhenFilterHasNoCriteria() {
        filter.setFilterCriteria(new ArrayList<>());

        when(recipeRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<RecipeResponseDto> result = recipeService.getFilteredRecipes(filter);

        assertTrue(result.isEmpty());
        verify(recipeRepository).findAll(any(Specification.class));
        verifyNoInteractions(recipeMapper);
    }

    @Test
    void getFilteredRecipes_shouldReturnEmptyListWhenNoRecipesMatchFilter() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setFilterKey("nonexistentKey");
        filter.setFilterCriteria(List.of(criteria));

        when(recipeRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<RecipeResponseDto> result = recipeService.getFilteredRecipes(filter);

        assertTrue(result.isEmpty());
        verify(recipeRepository).findAll(any(Specification.class));
        verifyNoInteractions(recipeMapper);
    }

    @Test
    void getRecipesByCategory_shouldReturnRecipesForCategory() {
        int categoryId = 1;
        RecipeCategoryAssociation association = new RecipeCategoryAssociation();
        association.setRecipe(recipe);

        when(recipeCategoryAssociationRepository.findByRecipeCategoryId(categoryId))
                .thenReturn(List.of(association));
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        List<RecipeResponseDto> result = recipeService.getRecipesByCategory(categoryId);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
        verify(recipeCategoryAssociationRepository).findByRecipeCategoryId(categoryId);
        verify(recipeMapper).toResponseDto(recipe);
    }

    @Test
    void getRecipesByCategory_shouldReturnEmptyListWhenNoRecipes() {
        int categoryId = 1;
        when(recipeCategoryAssociationRepository.findByRecipeCategoryId(categoryId))
                .thenReturn(new ArrayList<>());

        List<RecipeResponseDto> result = recipeService.getRecipesByCategory(categoryId);

        assertTrue(result.isEmpty());
        verify(recipeCategoryAssociationRepository).findByRecipeCategoryId(categoryId);
        verifyNoInteractions(recipeMapper);
    }
}

package source.code.unit.recipe;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.recipe.RecipeCreateDto;
import source.code.dto.request.recipe.RecipeUpdateDto;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.event.events.Recipe.RecipeCreateEvent;
import source.code.event.events.Recipe.RecipeDeleteEvent;
import source.code.event.events.Recipe.RecipeUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.repository.RecipeRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.recipe.RecipePopulationService;
import source.code.service.implementation.recipe.RecipeServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private RecipePopulationService recipePopulationService;
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
    private RecipeSummaryDto summaryDto;
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
        summaryDto = new RecipeSummaryDto();
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
        recipe.setId(recipeId);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(recipeMapper.toEntity(createDto, userId)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeRepository.findByIdWithDetails(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        RecipeResponseDto result = recipeService.createRecipe(createDto);

        assertEquals(responseDto, result);
        verify(recipePopulationService).populate(responseDto);
    }

    @Test
    void createRecipe_shouldPublishEvent() {
        ArgumentCaptor<RecipeCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(RecipeCreateEvent.class);
        recipe.setId(recipeId);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(recipeMapper.toEntity(createDto, userId)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeRepository.findByIdWithDetails(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        recipeService.createRecipe(createDto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(recipe, eventCaptor.getValue().getRecipe());
    }

    @Test
    void updateRecipe_shouldUpdateRecipe() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(recipeRepository, Recipe.class, recipeId)).thenReturn(recipe);
        when(jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class)).thenReturn(patchedDto);
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
        when(jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class)).thenReturn(patchedDto);
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
        when(jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class))
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
        when(jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class))
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
        when(recipeRepository.findByIdWithDetails(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toResponseDto(recipe)).thenReturn(responseDto);

        RecipeResponseDto result = recipeService.getRecipe(recipeId);

        assertEquals(responseDto, result);
        verify(recipePopulationService).populate(responseDto);
    }

    @Test
    void getRecipe_shouldThrowExceptionWhenRecipeNotFound() {
        when(recipeRepository.findByIdWithDetails(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> recipeService.getRecipe(recipeId));

        verifyNoInteractions(recipeMapper);
        verifyNoInteractions(recipePopulationService);
    }

    @Test
    void getAllRecipes_shouldReturnAllRecipes() {
        List<Recipe> recipes = List.of(recipe);
        Boolean isPrivate = true;
        int userId = 1;
        Pageable pageable = PageRequest.of(0, 100);
        Page<Recipe> recipePage = new PageImpl<>(recipes, pageable, recipes.size());

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(recipeRepository.findAllWithDetails(eq(isPrivate), eq(userId), eq(pageable))).thenReturn(recipePage);
        when(recipeMapper.toSummaryDto(recipe)).thenReturn(summaryDto);

        Page<RecipeSummaryDto> result = recipeService.getAllRecipes(isPrivate, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(summaryDto, result.getContent().get(0));
        verify(recipeRepository).findAllWithDetails(eq(isPrivate), eq(userId), eq(pageable));
        verify(recipeMapper).toSummaryDto(recipe);
        verify(recipePopulationService).populate(anyList());
    }

    @Test
    void getAllRecipes_shouldReturnEmptyPageWhenNoRecipes() {
        Boolean isPrivate = false;
        int userId = 1;
        Pageable pageable = PageRequest.of(0, 100);
        Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(recipeRepository.findAllWithDetails(eq(isPrivate), eq(userId), eq(pageable))).thenReturn(emptyPage);

        Page<RecipeSummaryDto> result = recipeService.getAllRecipes(isPrivate, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository).findAllWithDetails(eq(isPrivate), eq(userId), eq(pageable));
        verifyNoInteractions(recipeMapper);
        verify(recipePopulationService).populate(anyList());
    }

    @Test
    void getFilteredRecipes_shouldReturnFilteredRecipes() {
        List<Recipe> recipes = List.of(recipe);
        Pageable pageable = PageRequest.of(0, 100);
        Page<Recipe> recipePage = new PageImpl<>(recipes, pageable, recipes.size());

        when(recipeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(recipePage);
        when(recipeMapper.toSummaryDto(recipe)).thenReturn(summaryDto);

        Page<RecipeSummaryDto> result = recipeService.getFilteredRecipes(filter, pageable);

        assertEquals(1, result.getContent().size());
        assertSame(summaryDto, result.getContent().get(0));
        verify(recipeRepository).findAll(any(Specification.class), eq(pageable));
        verify(recipeMapper).toSummaryDto(recipe);
        verify(recipePopulationService).populate(anyList());
    }

    @Test
    void getFilteredRecipes_shouldReturnEmptyPageWhenFilterHasNoCriteria() {
        filter.setFilterCriteria(new ArrayList<>());
        Pageable pageable = PageRequest.of(0, 100);
        Page<Recipe> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(recipeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<RecipeSummaryDto> result = recipeService.getFilteredRecipes(filter, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository).findAll(any(Specification.class), eq(pageable));
        verify(recipePopulationService).populate(anyList());
    }

    @Test
    void getFilteredRecipes_shouldReturnEmptyPageWhenNoRecipesMatchFilter() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setFilterKey("nonexistentKey");
        filter.setFilterCriteria(List.of(criteria));
        Pageable pageable = PageRequest.of(0, 100);
        Page<Recipe> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(recipeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<RecipeSummaryDto> result = recipeService.getFilteredRecipes(filter, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(recipeRepository).findAll(any(Specification.class), eq(pageable));
        verify(recipePopulationService).populate(anyList());
    }

    @Test
    void incrementViews_shouldCallRepositoryIncrementViews() {
        recipeService.incrementViews(recipeId);

        verify(recipeRepository).incrementViews(recipeId);
    }

    @Test
    void incrementViews_shouldCallRepositoryWithCorrectId() {
        int specificRecipeId = 42;

        recipeService.incrementViews(specificRecipeId);

        verify(recipeRepository).incrementViews(specificRecipeId);
        verify(recipeRepository, never()).incrementViews(recipeId);
    }
}

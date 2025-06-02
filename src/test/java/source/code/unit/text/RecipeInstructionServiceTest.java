package source.code.unit.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import source.code.dto.request.text.RecipeInstructionUpdateDto;
import source.code.dto.response.text.BaseTextResponseDto;
import source.code.dto.response.text.RecipeInstructionResponseDto;
import source.code.event.events.Text.TextClearCacheEvent;
import source.code.event.events.Text.TextCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.text.TextMapper;
import source.code.model.text.RecipeInstruction;
import source.code.repository.RecipeInstructionRepository;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.helpers.ValidationServiceImpl;
import source.code.service.implementation.text.RecipeInstructionServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeInstructionServiceTest {
    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private JsonPatchServiceImpl jsonPatchService;

    @Mock
    private TextCacheKeyGenerator<RecipeInstruction> textCacheKeyGenerator;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private RecipeInstructionRepository repository;

    @Mock
    private TextMapper mapper;

    @InjectMocks
    private RecipeInstructionServiceImpl service;

    @Test
    @DisplayName("deleteText - Should delete a recipe instruction")
    public void deleteText() {
        int id = 1;

        when(repository.findById(id)).thenReturn(Optional.of(new RecipeInstruction()));
        doNothing().when(repository).delete(any(RecipeInstruction.class));
        doNothing().when(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));

        service.deleteText(id);

        verify(repository).findById(id);
        verify(repository).delete(any(RecipeInstruction.class));
        verify(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("deleteText - Should throw exception, when recipe instruction does not exist")
    public void deleteTextNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.deleteText(1));

        verify(repository).findById(1);
        verify(repository, never()).delete(any(RecipeInstruction.class));
        verify(applicationEventPublisher, never()).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("updateText - Should update a recipe instruction")
    public void updateText() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var updateDto = new RecipeInstructionUpdateDto();
        var entity = new RecipeInstruction();
        var responseDto = new RecipeInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toRecipeInstructionResponseDto(entity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(RecipeInstructionUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(mapper).updateRecipeInstruction(entity, updateDto);
        when(repository.save(entity)).thenReturn(entity);
        doNothing().when(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));

        service.updateText(id, mock(JsonMergePatch.class));

        verify(repository).findById(id);
        verify(jsonPatchService).applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(RecipeInstructionUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(mapper).updateRecipeInstruction(entity, updateDto);
        verify(repository).save(entity);
        verify(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("updateText - Should throw exception, when recipe instruction does not exist")
    public void updateTextNotFound() throws JsonPatchException, JsonProcessingException {
        int id = 1;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(repository).findById(id);
        verify(jsonPatchService, never()).applyPatch(any(), any(), any());
        verify(validationService, never()).validate(any());
        verify(mapper, never()).updateRecipeInstruction(any(), any());
        verify(repository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updateText - Should throw exception, when patch application fails")
    public void updateTextPatchFailed() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var entity = new RecipeInstruction();
        var responseDto = new RecipeInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toRecipeInstructionResponseDto(entity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(any(), any(), any()))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(validationService, never()).validate(any());
        verify(mapper, never()).updateRecipeInstruction(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateText - Should throw exception, when validation fails")
    public void updateTextValidationFailed() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var updateDto = new RecipeInstructionUpdateDto();
        var entity = new RecipeInstruction();
        var responseDto = new RecipeInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toRecipeInstructionResponseDto(entity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(RecipeInstructionUpdateDto.class)))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(repository).findById(id);
        verify(jsonPatchService).applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(RecipeInstructionUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(mapper, never()).updateRecipeInstruction(any(), any());
        verify(repository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("getAllByParent - Should return all recipe instructions for a given recipe (cache hit)")
    public void getAllByParentCacheHit() {
        int recipeId = 1;
        String cacheKey = "someCacheKey";
        List<BaseTextResponseDto> cachedResponse = List.of(new RecipeInstructionResponseDto());
        Cache.ValueWrapper cachedValue = mock(Cache.ValueWrapper.class);
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(recipeId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(cachedValue);
        when(cachedValue.get()).thenReturn(cachedResponse);

        List<BaseTextResponseDto> result = service.getAllByParent(recipeId);

        assertEquals(cachedResponse, result);
        verify(repository, never()).getAllByRecipeId(anyInt());
        verify(applicationEventPublisher, never()).publishEvent(any(TextCreateCacheEvent.class));
    }

    @Test
    @DisplayName("getAllByParent - Should return all recipe instructions for a given recipe (cache miss)")
    public void getAllByParentCacheMiss() {
        int recipeId = 1;
        String cacheKey = "someCacheKey";

        RecipeInstruction entity = new RecipeInstruction();
        RecipeInstructionResponseDto responseDto = new RecipeInstructionResponseDto();
        List<RecipeInstruction> entities = List.of(entity);
        List<BaseTextResponseDto> expectedResponse = List.of(responseDto);
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(recipeId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.getAllByRecipeId(recipeId)).thenReturn(entities);
        when(mapper.toRecipeInstructionResponseDto(entity)).thenReturn(responseDto);

        ArgumentCaptor<TextCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(TextCreateCacheEvent.class);

        List<BaseTextResponseDto> result = service.getAllByParent(recipeId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(repository).getAllByRecipeId(recipeId);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        TextCreateCacheEvent event = eventCaptor.getValue();
        assertEquals(cacheKey, event.getCacheKey());
        assertEquals(expectedResponse, event.getCachedData());
    }

    @Test
    @DisplayName("getAllByParent - Should return empty list when no recipe instructions exist")
    public void getAllByParentEmpty() {
        int recipeId = 1;
        String cacheKey = "someCacheKey";
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(recipeId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.getAllByRecipeId(recipeId)).thenReturn(Collections.emptyList());

        ArgumentCaptor<TextCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(TextCreateCacheEvent.class);

        List<BaseTextResponseDto> result = service.getAllByParent(recipeId);

        assertTrue(result.isEmpty());
        verify(repository).getAllByRecipeId(recipeId);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        TextCreateCacheEvent event = eventCaptor.getValue();
        assertEquals(cacheKey, event.getCacheKey());

        assertInstanceOf(List.class, event.getCachedData());
        List<?> cachedData = (List<?>) event.getCachedData();
        assertTrue(cachedData.isEmpty());
    }
}
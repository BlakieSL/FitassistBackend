package unit.text;

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
import source.code.dto.request.text.ExerciseInstructionUpdateDto;
import source.code.dto.response.text.BaseTextResponseDto;
import source.code.dto.response.text.ExerciseInstructionResponseDto;
import source.code.event.events.Text.TextClearCacheEvent;
import source.code.event.events.Text.TextCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.text.TextMapper;
import source.code.model.text.ExerciseInstruction;
import source.code.repository.ExerciseInstructionRepository;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.helpers.ValidationServiceImpl;
import source.code.service.implementation.text.ExerciseInstructionServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ExerciseInstructionServiceTest {
    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private JsonPatchServiceImpl jsonPatchService;

    @Mock
    private TextCacheKeyGenerator<ExerciseInstruction> textCacheKeyGenerator;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private ExerciseInstructionRepository repository;

    @Mock
    private TextMapper mapper;

    @InjectMocks
    private ExerciseInstructionServiceImpl service;

    @Test
    @DisplayName("deleteText - Should delete an exercise instruction")
    public void deleteText() {
        int id = 1;

        when(repository.findById(id)).thenReturn(Optional.of(new ExerciseInstruction()));
        doNothing().when(repository).delete(any(ExerciseInstruction.class));
        doNothing().when(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));

        service.deleteText(id);

        verify(repository).findById(id);
        verify(repository).delete(any(ExerciseInstruction.class));
        verify(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("deleteText - Should throw exception, when exercise instruction does not exist")
    public void deleteTextNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.deleteText(1));

        verify(repository).findById(1);
        verify(repository, never()).delete(any(ExerciseInstruction.class));
        verify(applicationEventPublisher, never()).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("updateText - Should update an exercise instruction")
    public void updateText() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var updateDto = new ExerciseInstructionUpdateDto();
        var entity = new ExerciseInstruction();
        var responseDto = new ExerciseInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toExerciseInstructionResponseDto(entity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(ExerciseInstructionUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(mapper).updateExerciseInstruction(entity, updateDto);
        when(repository.save(entity)).thenReturn(entity);
        doNothing().when(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));

        service.updateText(id, mock(JsonMergePatch.class));

        verify(repository).findById(id);
        verify(jsonPatchService).applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(ExerciseInstructionUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(mapper).updateExerciseInstruction(entity, updateDto);
        verify(repository).save(entity);
        verify(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("updateText - Should throw exception, when exercise instruction does not exist")
    public void updateTextNotFound() throws JsonPatchException, JsonProcessingException {
        int id = 1;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(repository).findById(id);
        verify(jsonPatchService, never()).applyPatch(any(), any(), any());
        verify(validationService, never()).validate(any());
        verify(mapper, never()).updateExerciseInstruction(any(), any());
        verify(repository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updateText - Should throw exception, when patch application fails")
    public void updateTextPatchFailed() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var entity = new ExerciseInstruction();
        var responseDto = new ExerciseInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toExerciseInstructionResponseDto(entity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(any(), any(), any()))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(validationService, never()).validate(any());
        verify(mapper, never()).updateExerciseInstruction(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateText - Should throw exception, when validation fails")
    public void updateTextValidationFailed() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var updateDto = new ExerciseInstructionUpdateDto();
        var entity = new ExerciseInstruction();
        var responseDto = new ExerciseInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toExerciseInstructionResponseDto(entity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(ExerciseInstructionUpdateDto.class)))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(repository).findById(id);
        verify(jsonPatchService).applyPatch(any(JsonMergePatch.class), eq(responseDto), eq(ExerciseInstructionUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(mapper, never()).updateExerciseInstruction(any(), any());
        verify(repository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("getAllByParent - Should return all exercise instructions for a given exercise(cache hit)")
    public void getAllByParent() {
        int exerciseId = 1;
        String cacheKey = "someCacheKey";
        List<BaseTextResponseDto> cachedResponse = List.of(new ExerciseInstructionResponseDto());
        Cache.ValueWrapper cachedValue = mock(Cache.ValueWrapper.class);
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(exerciseId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(cachedValue);
        when(cachedValue.get()).thenReturn(cachedResponse);

        List<BaseTextResponseDto> result = service.getAllByParent(exerciseId);

        assertEquals(cachedResponse, result);
        verify(repository, never()).getAllByExerciseId(anyInt());
        verify(applicationEventPublisher, never()).publishEvent(any(TextCreateCacheEvent.class));
    }

    @Test
    @DisplayName("getAllByParent - Should return all exercise instructions for a given exercise(cache miss)")
    public void getAllByParentCacheMiss() {
        int exerciseId = 1;
        String cacheKey = "someCacheKey";

        ExerciseInstruction entity = new ExerciseInstruction();
        ExerciseInstructionResponseDto responseDto = new ExerciseInstructionResponseDto();
        List<ExerciseInstruction> entities = List.of(entity);
        List<BaseTextResponseDto> expectedResponse = List.of(responseDto);
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(exerciseId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.getAllByExerciseId(exerciseId)).thenReturn(entities);
        when(mapper.toExerciseInstructionResponseDto(entity)).thenReturn(responseDto);

        ArgumentCaptor<TextCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(TextCreateCacheEvent.class);

        List<BaseTextResponseDto> result = service.getAllByParent(exerciseId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(repository).getAllByExerciseId(exerciseId);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        TextCreateCacheEvent event = eventCaptor.getValue();
        assertEquals(cacheKey, event.getCacheKey());
        assertEquals(expectedResponse, event.getCachedData());
    }

    @Test
    @DisplayName("getAllByParent - Should return empty list when no exercise instructions exist")
    public void getAllByParentEmpty() {
        int exerciseId = 1;
        String cacheKey = "someCacheKey";
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(exerciseId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.getAllByExerciseId(exerciseId)).thenReturn(Collections.emptyList());

        ArgumentCaptor<TextCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(TextCreateCacheEvent.class);

        List<BaseTextResponseDto> result = service.getAllByParent(exerciseId);

        assertTrue(result.isEmpty());
        verify(repository).getAllByExerciseId(exerciseId);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        TextCreateCacheEvent event = eventCaptor.getValue();
        assertEquals(cacheKey, event.getCacheKey());

        assertInstanceOf(List.class, event.getCachedData());
        List<?> cachedData = (List<?>) event.getCachedData();
        assertTrue(cachedData.isEmpty());
    }
}

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
import source.code.dto.request.text.PlanInstructionUpdateDto;
import source.code.dto.response.text.BaseTextResponseDto;
import source.code.dto.response.text.PlanInstructionResponseDto;
import source.code.event.events.Text.TextClearCacheEvent;
import source.code.event.events.Text.TextCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.text.TextMapper;
import source.code.model.text.PlanInstruction;
import source.code.repository.PlanInstructionRepository;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.helpers.ValidationServiceImpl;
import source.code.service.implementation.text.PlanInstructionServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanInstructionServiceTest {
    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private JsonPatchServiceImpl jsonPatchService;

    @Mock
    private TextCacheKeyGenerator<PlanInstruction> textCacheKeyGenerator;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private PlanInstructionRepository repository;

    @Mock
    private TextMapper mapper;

    @InjectMocks
    private PlanInstructionServiceImpl service;

    @Test
    @DisplayName("deleteText - Should delete a plan instruction")
    public void deleteText() {
        int id = 1;

        when(repository.findById(id)).thenReturn(Optional.of(new PlanInstruction()));
        doNothing().when(repository).delete(any(PlanInstruction.class));
        doNothing().when(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));

        service.deleteText(id);

        verify(repository).findById(id);
        verify(repository).delete(any(PlanInstruction.class));
        verify(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("deleteText - Should throw exception, when plan instruction does not exist")
    public void deleteTextNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.deleteText(1));

        verify(repository).findById(1);
        verify(repository, never()).delete(any(PlanInstruction.class));
        verify(applicationEventPublisher, never()).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("updateText - Should update a plan instruction")
    public void updateText() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var updateDto = new PlanInstructionUpdateDto();
        var entity = new PlanInstruction();
        var responseDto = new PlanInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(jsonPatchService.createFromPatch(any(), any()))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(mapper).updatePlanInstruction(entity, updateDto);
        when(repository.save(entity)).thenReturn(entity);
        doNothing().when(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));

        service.updateText(id, mock(JsonMergePatch.class));

        verify(repository).findById(id);
        verify(jsonPatchService).createFromPatch(any(), any());
        verify(validationService).validate(updateDto);
        verify(mapper).updatePlanInstruction(entity, updateDto);
        verify(repository).save(entity);
        verify(applicationEventPublisher).publishEvent(any(TextClearCacheEvent.class));
    }

    @Test
    @DisplayName("updateText - Should throw exception, when plan instruction does not exist")
    public void updateTextNotFound() throws JsonPatchException, JsonProcessingException {
        int id = 1;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(repository).findById(id);
        verify(jsonPatchService, never()).createFromPatch(any(), any());
        verify(validationService, never()).validate(any());
        verify(mapper, never()).updatePlanInstruction(any(), any());
        verify(repository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updateText - Should throw exception, when patch application fails")
    public void updateTextPatchFailed() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var entity = new PlanInstruction();
        var responseDto = new PlanInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(jsonPatchService.createFromPatch(any(), any()))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(validationService, never()).validate(any());
        verify(mapper, never()).updatePlanInstruction(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateText - Should throw exception, when validation fails")
    public void updateTextValidationFailed() throws JsonPatchException, JsonProcessingException {
        int id = 1;
        var updateDto = new PlanInstructionUpdateDto();
        var entity = new PlanInstruction();
        var responseDto = new PlanInstructionResponseDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(jsonPatchService.createFromPatch(any(), any()))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> service.updateText(id, mock(JsonMergePatch.class)));

        verify(repository).findById(id);
        verify(jsonPatchService).createFromPatch(any(), any());
        verify(validationService).validate(updateDto);
        verify(mapper, never()).updatePlanInstruction(any(), any());
        verify(repository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("getAllByParent - Should return all plan instructions for a given plan (cache hit)")
    public void getAllByParentCacheHit() {
        int planId = 1;
        String cacheKey = "someCacheKey";
        List<BaseTextResponseDto> cachedResponse = List.of(new PlanInstructionResponseDto());
        Cache.ValueWrapper cachedValue = mock(Cache.ValueWrapper.class);
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(planId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(cachedValue);
        when(cachedValue.get()).thenReturn(cachedResponse);

        List<BaseTextResponseDto> result = service.getAllByParent(planId);

        assertEquals(cachedResponse, result);
        verify(repository, never()).getAllByPlanId(anyInt());
        verify(applicationEventPublisher, never()).publishEvent(any(TextCreateCacheEvent.class));
    }

    @Test
    @DisplayName("getAllByParent - Should return all plan instructions for a given plan (cache miss)")
    public void getAllByParentCacheMiss() {
        int planId = 1;
        String cacheKey = "someCacheKey";

        PlanInstruction entity = new PlanInstruction();
        PlanInstructionResponseDto responseDto = new PlanInstructionResponseDto();
        List<PlanInstruction> entities = List.of(entity);
        List<BaseTextResponseDto> expectedResponse = List.of(responseDto);
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(planId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.getAllByPlanId(planId)).thenReturn(entities);
        when(mapper.toPlanInstructionResponseDto(entity)).thenReturn(responseDto);

        ArgumentCaptor<TextCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(TextCreateCacheEvent.class);

        List<BaseTextResponseDto> result = service.getAllByParent(planId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(repository).getAllByPlanId(planId);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        TextCreateCacheEvent event = eventCaptor.getValue();
        assertEquals(cacheKey, event.getCacheKey());
        assertEquals(expectedResponse, event.getCachedData());
    }

    @Test
    @DisplayName("getAllByParent - Should return empty list when no plan instructions exist")
    public void getAllByParentEmpty() {
        int planId = 1;
        String cacheKey = "someCacheKey";
        Cache cache = mock(Cache.class);

        when(textCacheKeyGenerator.generateCacheKeyForParent(planId)).thenReturn(cacheKey);
        when(cacheManager.getCache("allTextByParent")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.getAllByPlanId(planId)).thenReturn(Collections.emptyList());

        ArgumentCaptor<TextCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(TextCreateCacheEvent.class);

        List<BaseTextResponseDto> result = service.getAllByParent(planId);

        assertTrue(result.isEmpty());
        verify(repository).getAllByPlanId(planId);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        TextCreateCacheEvent event = eventCaptor.getValue();
        assertEquals(cacheKey, event.getCacheKey());

        assertInstanceOf(List.class, event.getCachedData());
        List<?> cachedData = (List<?>) event.getCachedData();
        assertTrue(cachedData.isEmpty());
    }
}
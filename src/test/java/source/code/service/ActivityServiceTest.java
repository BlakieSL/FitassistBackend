package source.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.ActivityUpdateDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.event.events.Activity.ActivityCreateEvent;
import source.code.event.events.Activity.ActivityUpdateEvent;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.repository.ActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.activity.ActivityServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private ValidationService validationService;
    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ActivityServiceImpl activityService;

    private Activity activity;
    private JsonMergePatch patch;
    @BeforeEach
    void setUp() {
        activity = new Activity();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    void createActivity_shouldCreateActivityAndPublish() {
        ActivityCreateDto dto = new ActivityCreateDto();
        Activity activity = new Activity();
        ActivityResponseDto responseDto = new ActivityResponseDto();
        ArgumentCaptor<ActivityCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityCreateEvent.class);

        when(activityMapper.toEntity(dto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        ActivityResponseDto result = activityService.createActivity(dto);

        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        verify(activityMapper, times(1)).toEntity(dto);
        verify(activityRepository, times(1)).save(activity);
        assertEquals(responseDto, result);
        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void createActivity_shouldThrowExceptionWhenMappingFails() {
        ActivityCreateDto dto = new ActivityCreateDto();

        when(activityMapper.toEntity(dto)).thenThrow(new RuntimeException("Mapping failed"));

        assertThrows(RuntimeException.class, () -> activityService.createActivity(dto));
    }

    @Test
    void createActivity_shouldThrowExceptionWhenSaveFails() {
        ActivityCreateDto dto = new ActivityCreateDto();
        Activity activity = new Activity();

        when(activityMapper.toEntity(dto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> activityService.createActivity(dto));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void createActivity_shouldThrowExceptionWhenResponseMappingFails() {
        ActivityCreateDto dto = new ActivityCreateDto();
        Activity activity = new Activity();

        when(activityMapper.toEntity(dto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity))
                .thenThrow(new RuntimeException("Response mapping failed"));

        assertThrows(RuntimeException.class, () -> activityService.createActivity(dto));
    }

    @Test
    void updateActivity_shouldApplyPatchValidateSaveAndPublishEvent()
            throws JsonPatchException, JsonProcessingException
    {
        int activityId = 1;
        ActivityUpdateDto patchedDto = new ActivityUpdateDto();
        ArgumentCaptor<ActivityUpdateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityUpdateEvent.class);

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.applyPatch(
                patch,
                activityMapper.toResponseDto(activity),
                ActivityUpdateDto.class)
        ).thenReturn(patchedDto);

        when(activityRepository.save(activity)).thenReturn(activity);

        // Act
        activityService.updateActivity(activityId, patch);

        // Assert
        verify(jsonPatchService).applyPatch(
                patch,
                activityMapper.toResponseDto(activity),
                ActivityUpdateDto.class
        );
        verify(validationService).validate(patchedDto);
        verify(activityMapper).updateActivityFromDto(activity, patchedDto);
        verify(activityRepository).save(activity);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertEquals(activity, eventCaptor.getValue().getActivity());
    }


    @Test
    void updateActivity_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        int activityId = 1;

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.applyPatch(
                patch,
                activityMapper.toResponseDto(activity),
                ActivityUpdateDto.class)
        ).thenThrow(new JsonPatchException("Patch error"));


        assertThrows(JsonPatchException.class, () -> activityService
                .updateActivity(activityId, patch));
        verify(validationService, never()).validate(any());
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateActivity_shouldNotSaveOrPublishWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        int activityId = 1;
        ActivityUpdateDto patchedDto = new ActivityUpdateDto();

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.applyPatch(
                patch,
                activityMapper.toResponseDto(activity),
                ActivityUpdateDto.class)
        ).thenReturn(patchedDto);

        doThrow(new RuntimeException("Validation failed")).when(validationService).validate(patchedDto);


        assertThrows(RuntimeException.class, () -> activityService.updateActivity(activityId, patch));
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateActivity_shouldNotPublishEventWhenSaveFails()
            throws JsonPatchException, JsonProcessingException
    {
        int activityId = 1;
        ActivityUpdateDto patchedDto = new ActivityUpdateDto();

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.applyPatch(
                patch,
                activityMapper.toResponseDto(activity),
                ActivityUpdateDto.class)
        ).thenReturn(patchedDto);

        doThrow(new RuntimeException("Database error")).when(activityRepository).save(activity);

        assertThrows(RuntimeException.class, () -> activityService.updateActivity(activityId, patch));
        verify(eventPublisher, never()).publishEvent(any());
    }

}

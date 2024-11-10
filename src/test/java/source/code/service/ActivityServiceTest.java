package source.code.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.event.events.Activity.ActivityCreateEvent;
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

    @BeforeEach
    void setUp() {
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

        assertEquals(responseDto, result);
        assertEquals(activity, eventCaptor.getValue().getActivity());
        verify(activityMapper, times(1)).toEntity(dto);
        verify(activityRepository, times(1)).save(activity);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
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


}

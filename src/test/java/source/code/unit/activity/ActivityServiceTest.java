package source.code.unit.activity;

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
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.ActivityUpdateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.event.events.Activity.ActivityCreateEvent;
import source.code.event.events.Activity.ActivityDeleteEvent;
import source.code.event.events.Activity.ActivityUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.activity.ActivityServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
    private ActivityCreateDto createDto;
    private ActivityResponseDto responseDto;
    private JsonMergePatch patch;
    private ActivityUpdateDto patchedDto;
    private int activityId;
    private int userId;
    private User user;
    private ActivityCalculatedResponseDto calculatedResponseDto;
    private CalculateActivityCaloriesRequestDto calculateRequestDto;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;
    private FilterDto filter;
    @BeforeEach
    void setUp() {
        activity = new Activity();
        createDto = new ActivityCreateDto();
        responseDto = new ActivityResponseDto();
        patchedDto = new ActivityUpdateDto();
        activityId = 1;
        userId = 1;
        user = new User();
        calculatedResponseDto = new ActivityCalculatedResponseDto();
        calculateRequestDto = new CalculateActivityCaloriesRequestDto();
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
        filter = new FilterDto();
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void createActivity_shouldCreateActivityAndPublish() {
        when(activityMapper.toEntity(createDto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        ActivityResponseDto result = activityService.createActivity(createDto);

        assertEquals(responseDto, result);
    }

    @Test
    void createActivity_shouldPublishEvent() {
        ArgumentCaptor<ActivityCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityCreateEvent.class);

        when(activityMapper.toEntity(createDto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        activityService.createActivity(createDto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void updateActivity_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class))
                .thenReturn(patchedDto);
        when(activityRepository.save(activity)).thenReturn(activity);

        activityService.updateActivity(activityId, patch);

        verify(validationService).validate(patchedDto);
        verify(activityMapper).updateActivityFromDto(activity, patchedDto);
        verify(activityRepository).save(activity);
    }

    @Test
    void updateActivity_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
        ArgumentCaptor<ActivityUpdateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityUpdateEvent.class);

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class))
                .thenReturn(patchedDto);
        when(activityRepository.save(activity)).thenReturn(activity);

        activityService.updateActivity(activityId, patch);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void updateActivity_shouldThrowExceptionWhenActivityNotFound() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .updateActivity(activityId, patch));

        verifyNoInteractions(
                activityMapper,
                jsonPatchService,
                validationService,
                activityMapper,
                eventPublisher
        );
        verify(activityRepository, never()).save(activity);
    }

    @Test
    void updateActivity_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class))
                .thenThrow(JsonPatchException.class);


        assertThrows(JsonPatchException.class, () -> activityService
                .updateActivity(activityId, patch));

        verifyNoInteractions(validationService, eventPublisher);
        verify(activityRepository, never()).save(activity);
    }

    @Test
    void updateActivity_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () ->
                activityService.updateActivity(activityId, patch));
        verify(validationService).validate(patchedDto);
        verifyNoInteractions(eventPublisher);
        verify(activityRepository, never()).save(activity);
    }

    @Test
    void deleteActivity_shouldDelete() {
        when(activityRepository.findByIdWithAssociations(activityId))
                .thenReturn(Optional.of(activity));

        activityService.deleteActivity(activityId);

        verify(activityRepository).delete(activity);
    }

    @Test
    void deleteActivity_shouldPublishEvent() {
        ArgumentCaptor<ActivityDeleteEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityDeleteEvent.class);

        when(activityRepository.findByIdWithAssociations(activityId))
                .thenReturn(Optional.of(activity));

        activityService.deleteActivity(activityId);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void deleteActivity_shouldThrowExceptionWhenActivityNotFound() {
        when(activityRepository.findByIdWithAssociations(activityId))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> activityService
                .deleteActivity(activityId));

        verify(activityRepository, never()).delete(activity);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void calculateCaloriesBurned_shouldCalculateCaloriesForActivityWeightAlreadySaved() {
        user.setWeight(BigDecimal.valueOf(80));
        when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(activityMapper.toCalculatedDto(activity, BigDecimal.valueOf(80), calculateRequestDto.getTime()))
                .thenReturn(calculatedResponseDto);

        ActivityCalculatedResponseDto result = activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto);

        verify(activityMapper).toCalculatedDto(activity, BigDecimal.valueOf(80), calculateRequestDto.getTime());
        assertEquals(calculatedResponseDto, result);
    }

    @Test
    void calculateCaloriesBurned_shouldCalculateCaloriesForActivityWeightInRequest() {
        calculateRequestDto.setWeight(BigDecimal.valueOf(80));
        when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
        when(activityMapper.toCalculatedDto(activity, BigDecimal.valueOf(80), calculateRequestDto.getTime()))
                .thenReturn(calculatedResponseDto);

        ActivityCalculatedResponseDto result = activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto);

        verify(activityMapper).toCalculatedDto(activity, BigDecimal.valueOf(80), calculateRequestDto.getTime());
        assertEquals(calculatedResponseDto, result);
    }

    @Test
    void calculateCaloriesBurned_shouldCalculateCaloriesForActivityUsingWeightInRequestEvenIfAlreadySaved() {
        user.setWeight(BigDecimal.valueOf(80));
        calculateRequestDto.setWeight(BigDecimal.valueOf(80));
        when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
        when(activityMapper.toCalculatedDto(activity, BigDecimal.valueOf(80), calculateRequestDto.getTime()))
                .thenReturn(calculatedResponseDto);

        ActivityCalculatedResponseDto result = activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto);

        verify(activityMapper).toCalculatedDto(activity, BigDecimal.valueOf(80), calculateRequestDto.getTime());
        assertEquals(calculatedResponseDto, result);
    }

    @Test
    void calculateCaloriesBurned_shouldNotProceedWhenActivityNotFound() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto));

        verifyNoInteractions(activityMapper);
    }

    @Test
    void getActivity_shouldReturnActivityWhenFound() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        ActivityResponseDto result = activityService.getActivity(activityId);

        assertEquals(responseDto, result);
    }

    @Test
    void getActivity_shouldNotProceedWhenActivityNotFound() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .getActivity(activityId));
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAllActivities_shouldReturnAllActivities() {
        List<Activity> activities = List.of(activity);
        List<ActivityResponseDto> responseDtos = List.of(responseDto);

        when(activityRepository.findAllWithActivityCategory())
                .thenReturn(activities);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        List<ActivityResponseDto> result = activityService.getAllActivities();

        assertEquals(responseDtos, result);
        verify(activityRepository).findAllWithActivityCategory();
    }

    @Test
    void getAllActivities_shouldReturnEmptyListWhenNoActivities() {
        List<Activity> activities = List.of();
        when(activityRepository.findAllWithActivityCategory())
                .thenReturn(activities);

        List<ActivityResponseDto> result = activityService.getAllActivities();

        assertTrue(result.isEmpty());
        verify(activityRepository).findAllWithActivityCategory();
    }

    @Test
    void getFilteredActivities_shouldReturnFilteredActivities() {
        when(activityRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(activity));
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        List<ActivityResponseDto> result = activityService.getFilteredActivities(filter);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
        verify(activityRepository).findAll(any(Specification.class));
        verify(activityMapper).toResponseDto(activity);
    }

    @Test
    void getFilteredActivities_shouldReturnEmptyListWhenFilterHasNoCriteria() {
        filter.setFilterCriteria(new ArrayList<>());

        when(activityRepository.findAll(any(Specification.class)))
                .thenReturn(new ArrayList<>());

        List<ActivityResponseDto> result = activityService.getFilteredActivities(filter);

        assertTrue(result.isEmpty());
        verify(activityRepository).findAll(any(Specification.class));
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getFilteredActivities_shouldReturnEmptyListWhenNoActivitiesMatchFilter() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setFilterKey("nonexistentKey");
        filter.setFilterCriteria(List.of(criteria));

        when(activityRepository.findAll(any(Specification.class)))
                .thenReturn(new ArrayList<>());

        List<ActivityResponseDto> result = activityService.getFilteredActivities(filter);

        assertTrue(result.isEmpty());
        verify(activityRepository).findAll(any(Specification.class));
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAllActivityEntities_shouldReturnAllActivityEntities() {
        List<Activity> activities = List.of(activity);
        when(activityRepository.findAllWithoutAssociations()).thenReturn(activities);

        List<Activity> result = activityService.getAllActivityEntities();

        assertEquals(activities, result);
        verify(activityRepository).findAllWithoutAssociations();
    }

    @Test
    void getAllActivityEntities_shouldReturnEmptyListWhenNoActivities() {
        List<Activity> activities = List.of();
        when(activityRepository.findAllWithoutAssociations()).thenReturn(activities);

        List<Activity> result = activityService.getAllActivityEntities();

        assertTrue(result.isEmpty());
        verify(activityRepository).findAllWithoutAssociations();
    }
}

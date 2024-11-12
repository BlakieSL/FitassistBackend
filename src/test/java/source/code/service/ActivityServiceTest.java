package source.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.activity.ActivityCreateDto;
import source.code.dto.request.activity.ActivityUpdateDto;
import source.code.dto.request.activity.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.response.activity.ActivityAverageMetResponseDto;
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
import source.code.specification.SpecificationBuilder;
import source.code.specification.SpecificationFactory;
import source.code.specification.specification.ActivitySpecification;

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
        ArgumentCaptor<ActivityCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityCreateEvent.class);

        when(activityMapper.toEntity(createDto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        ActivityResponseDto result = activityService.createActivity(createDto);

        verify(activityMapper).toEntity(createDto);
        verify(activityRepository).save(activity);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        verify(activityMapper).toResponseDto(activity);
        assertEquals(responseDto, result);
        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void createActivity_shouldThrowExceptionWhenMappingFails() {
        when(activityMapper.toEntity(createDto)).thenThrow(new RuntimeException("Mapping failed"));

        assertThrows(RuntimeException.class, () -> activityService.createActivity(createDto));
        verify(activityMapper).toEntity(createDto);
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
        verify(activityMapper, never()).toResponseDto(any());
    }

    @Test
    void createActivity_shouldThrowExceptionWhenSaveFails() {
        when(activityMapper.toEntity(createDto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> activityService.createActivity(createDto));
        verify(activityMapper).toEntity(createDto);
        verify(activityRepository).save(activity);
        verify(eventPublisher, never()).publishEvent(any());
        verify(activityMapper, never()).toResponseDto(any());
    }

    @Test
    void createActivity_shouldThrowExceptionWhenResponseMappingFails() {
        ArgumentCaptor<ActivityCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityCreateEvent.class);

        when(activityMapper.toEntity(createDto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity))
                .thenThrow(new RuntimeException("Response mapping failed"));

        assertThrows(RuntimeException.class, () -> activityService.createActivity(createDto));
        verify(activityMapper).toEntity(createDto);
        verify(activityRepository).save(activity);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        verify(activityMapper).toResponseDto(activity);
    }

    @Test
    void updateActivity_shouldApplyPatchValidateSaveAndPublishEvent()
            throws JsonPatchException, JsonProcessingException
    {
        ArgumentCaptor<ActivityUpdateEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityUpdateEvent.class);

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, ActivityUpdateDto.class))
                .thenReturn(patchedDto);
        doNothing().when(validationService).validate(patchedDto);
        doNothing().when(activityMapper).updateActivityFromDto(activity, patchedDto);
        when(activityRepository.save(activity)).thenReturn(activity);

        activityService.updateActivity(activityId, patch);

        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper).toResponseDto(activity);
        verify(jsonPatchService)
                .applyPatch(patch, activityMapper.toResponseDto(activity), ActivityUpdateDto.class);
        verify(validationService, times(1)).validate(patchedDto);
        verify(activityMapper).updateActivityFromDto(activity, patchedDto);
        verify(activityRepository).save(activity);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void updateActivity_shouldNotProceedWhenFindFails()
        throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .updateActivity(activityId, patch));
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper, never()).toResponseDto(any());
        verify(jsonPatchService, never()).applyPatch(any(), any(), any());
        verify(validationService, never()).validate(any());
        verify(activityMapper, never()).updateActivityFromDto(any(), any());
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateActivity_shouldNotProceedWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, ActivityUpdateDto.class))
                .thenThrow(new JsonPatchException("Patch error"));


        assertThrows(JsonPatchException.class, () -> activityService
                .updateActivity(activityId, patch));
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper).toResponseDto(activity);
        verify(jsonPatchService).applyPatch(patch, responseDto, ActivityUpdateDto.class);
        verify(validationService, never()).validate(any());
        verify(activityMapper, never()).updateActivityFromDto(any(), any());
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateActivity_shouldNotProceedWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, ActivityUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new RuntimeException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () -> activityService.updateActivity(activityId, patch));
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper).toResponseDto(activity);
        verify(jsonPatchService).applyPatch(patch, responseDto, ActivityUpdateDto.class);
        verify(validationService).validate(patchedDto);
        verify(activityMapper, never()).updateActivityFromDto(any(), any());
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateActivity_shouldNotProceedWhenSaveFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);
        when(jsonPatchService.applyPatch(patch, responseDto, ActivityUpdateDto.class))
                .thenReturn(patchedDto);
        doNothing().when(validationService).validate(patchedDto);

        doThrow(new RuntimeException("Database error")).when(activityRepository).save(activity);

        assertThrows(RuntimeException.class, () -> activityService.updateActivity(activityId, patch));
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper).toResponseDto(activity);
        verify(jsonPatchService).applyPatch(patch, responseDto, ActivityUpdateDto.class);
        verify(validationService).validate(patchedDto);
        verify(activityMapper).updateActivityFromDto(activity, patchedDto);
        verify(activityRepository).save(activity);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteActivity_shouldDeleteActivityAndPublishEvent() {
        ArgumentCaptor<ActivityDeleteEvent> eventCaptor = ArgumentCaptor
                .forClass(ActivityDeleteEvent.class);

        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);

        activityService.deleteActivity(activityId);

        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityRepository).delete(activity);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertEquals(activity, eventCaptor.getValue().getActivity());
    }

    @Test
    void deleteActivity_shouldNotProceedWhenActivityNotFound() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .deleteActivity(activityId));

        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityRepository, never()).delete((Activity) any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteActivity_shouldThrowExceptionWhenDeleteFails() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        doThrow(new RuntimeException("Database error")).when(activityRepository).delete(activity);

        assertThrows(RuntimeException.class, () -> activityService.deleteActivity(activityId));

        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityRepository).delete(activity);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void calculateCaloriesBurned_shouldCalculateCaloriesForActivity() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
        when(activityMapper.toCalculatedDto(activity, user, calculateRequestDto.getTime()))
                .thenReturn(calculatedResponseDto);

        ActivityCalculatedResponseDto result = activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto);

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper).toCalculatedDto(activity, user, calculateRequestDto.getTime());

        assertEquals(calculatedResponseDto, result);
    }

    @Test
    void calculateCaloriesBurned_shouldNotProceedWhenUserNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(repositoryHelper.find(userRepository, User.class, userId))
                .thenThrow(RecordNotFoundException.of(User.class, userId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto));

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(repositoryHelper, never()).find(activityRepository, Activity.class, activityId);
        verify(activityMapper, never()).toCalculatedDto(any(), any(), anyInt());
    }

    @Test
    void calculateCaloriesBurned_shouldNotProceedWhenActivityNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);

        when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .calculateCaloriesBurned(activityId, calculateRequestDto));

        verify(repositoryHelper).find(userRepository, User.class, userId);
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper, never()).toCalculatedDto(any(), any(), anyInt());
    }

    @Test
    void getActivity_shouldFetchActivityAndMapToResponseDto() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        ActivityResponseDto result = activityService.getActivity(activityId);

        assertEquals(responseDto, result);
        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper).toResponseDto(activity);
    }

    @Test
    void getActivity_shouldNotProceedWhenActivityNotFound() {
        when(repositoryHelper.find(activityRepository, Activity.class, activityId))
                .thenThrow(RecordNotFoundException.of(Activity.class, activityId));

        assertThrows(RecordNotFoundException.class, () -> activityService
                .deleteActivity(activityId));

        verify(repositoryHelper).find(activityRepository, Activity.class, activityId);
        verify(activityMapper, never()).toResponseDto(any());
    }

    @Test
    void getAllActivities_shouldReturnAllActivities() {
        List<ActivityResponseDto> responseDtos = List.of(responseDto);

        when(repositoryHelper.findAll(eq(activityRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<ActivityResponseDto> result = activityService.getAllActivities();


        assertEquals(responseDtos, result);
        verify(repositoryHelper).findAll(eq(activityRepository), any(Function.class));
    }

    @Test
    void getAllActivities_shouldReturnEmptyListWhenNoActivities() {
        List<ActivityResponseDto> responseDtos = List.of();
        when(repositoryHelper.findAll(eq(activityRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<ActivityResponseDto> result = activityService.getAllActivities();

        assertTrue(result.isEmpty());
        verify(repositoryHelper).findAll(eq(activityRepository), any(Function.class));
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

    @Test
    void getActivitiesByCategory_shouldReturnActivitiesForCategory() {
        int categoryId = 1;
        List<Activity> activities = List.of(activity);
        when(activityRepository.findAllByActivityCategory_Id(categoryId)).thenReturn(activities);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        List<ActivityResponseDto> result = activityService.getActivitiesByCategory(categoryId);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
        verify(activityRepository).findAllByActivityCategory_Id(categoryId);
        verify(activityMapper).toResponseDto(activity);
    }

    @Test
    void getActivitiesByCategory_shouldReturnEmptyListWhenNoActivities() {
        int categoryId = 1;
        when(activityRepository.findAllByActivityCategory_Id(categoryId)).thenReturn(new ArrayList<>());

        List<ActivityResponseDto> result = activityService.getActivitiesByCategory(categoryId);

        assertTrue(result.isEmpty());
        verify(activityRepository).findAllByActivityCategory_Id(categoryId);
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAverageMet_shouldReturnAverageMet() {
        activity.setMet(5.0);
        when(activityRepository.findAll()).thenReturn(List.of(activity));

        ActivityAverageMetResponseDto result = activityService.getAverageMet();

        assertEquals(5.0, result.getMet());
        verify(activityRepository).findAll();
    }

    @Test
    void getAverageMet_shouldReturnZeroWhenNoActivities() {
        when(activityRepository.findAll()).thenReturn(new ArrayList<>());

        ActivityAverageMetResponseDto result = activityService.getAverageMet();

        assertEquals(0.0, result.getMet());
        verify(activityRepository).findAll();
    }
}

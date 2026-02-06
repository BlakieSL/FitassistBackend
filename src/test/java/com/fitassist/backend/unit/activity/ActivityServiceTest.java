package com.fitassist.backend.unit.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.dto.request.activity.ActivityCreateDto;
import com.fitassist.backend.dto.request.activity.ActivityUpdateDto;
import com.fitassist.backend.dto.request.activity.CalculateActivityCaloriesRequestDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.activity.ActivityResponseDto;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.event.event.Activity.ActivityCreateEvent;
import com.fitassist.backend.event.event.Activity.ActivityDeleteEvent;
import com.fitassist.backend.event.event.Activity.ActivityUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.activity.ActivityMapper;
import com.fitassist.backend.mapper.activity.ActivityMappingContext;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.activity.ActivityCategory;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.ActivityCategoryRepository;
import com.fitassist.backend.repository.ActivityRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.activity.ActivityPopulationService;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.activity.ActivityServiceImpl;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private ActivityCategoryRepository activityCategoryRepository;

	@Mock
	private ActivityPopulationService activityPopulationService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SpecificationDependencies dependencies;

	@Mock
	private CalculationsService calculationsService;

	@InjectMocks
	private ActivityServiceImpl activityService;

	private Activity activity;

	private ActivityCategory activityCategory;

	private ActivityCreateDto createDto;

	private ActivitySummaryDto responseDto;

	private ActivityResponseDto detailedResponseDto;

	private JsonMergePatch patch;

	private ActivityUpdateDto patchedDto;

	private int activityId;

	private int userId;

	private int categoryId;

	private User user;

	private ActivityCalculatedResponseDto calculatedResponseDto;

	private CalculateActivityCaloriesRequestDto calculateRequestDto;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	private FilterDto filter;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		activity = new Activity();
		activityCategory = new ActivityCategory();
		createDto = new ActivityCreateDto();
		responseDto = new ActivitySummaryDto();
		detailedResponseDto = new ActivityResponseDto();
		patchedDto = new ActivityUpdateDto();
		activityId = 1;
		userId = 1;
		categoryId = 1;
		user = new User();
		calculatedResponseDto = new ActivityCalculatedResponseDto();
		calculateRequestDto = new CalculateActivityCaloriesRequestDto();
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
		filter = new FilterDto();
		pageable = PageRequest.of(0, 100);

		createDto.setCategoryId(categoryId);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void createActivity_shouldCreateActivityAndPublish() {
		activity.setId(activityId);
		when(activityCategoryRepository.findById(categoryId)).thenReturn(Optional.of(activityCategory));
		when(activityMapper.toEntity(eq(createDto), any(ActivityMappingContext.class))).thenReturn(activity);
		when(activityRepository.save(activity)).thenReturn(activity);
		when(activityRepository.findByIdWithMedia(activityId)).thenReturn(Optional.of(activity));
		when(activityMapper.toResponse(activity)).thenReturn(detailedResponseDto);

		ActivityResponseDto result = activityService.createActivity(createDto);

		assertEquals(detailedResponseDto, result);
		verify(activityPopulationService).populate(detailedResponseDto);
	}

	@Test
	void createActivity_shouldPublishEvent() {
		ArgumentCaptor<ActivityCreateEvent> eventCaptor = ArgumentCaptor.forClass(ActivityCreateEvent.class);

		activity.setId(activityId);
		when(activityCategoryRepository.findById(categoryId)).thenReturn(Optional.of(activityCategory));
		when(activityMapper.toEntity(eq(createDto), any(ActivityMappingContext.class))).thenReturn(activity);
		when(activityRepository.save(activity)).thenReturn(activity);
		when(activityRepository.findByIdWithMedia(activityId)).thenReturn(Optional.of(activity));
		when(activityMapper.toResponse(activity)).thenReturn(detailedResponseDto);

		activityService.createActivity(createDto);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(activity, eventCaptor.getValue().getActivity());
	}

	@Test
	void createActivity_shouldThrowExceptionWhenCategoryNotFound() {
		when(activityCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> activityService.createActivity(createDto));

		verify(activityRepository, never()).save(any());
	}

	@Test
	void updateActivity_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		activity.setId(activityId);
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class)).thenReturn(patchedDto);
		when(activityRepository.save(activity)).thenReturn(activity);
		when(activityRepository.findByIdWithMedia(activityId)).thenReturn(Optional.of(activity));

		activityService.updateActivity(activityId, patch);

		verify(validationService).validate(patchedDto);
		verify(activityMapper).update(eq(activity), eq(patchedDto), any(ActivityMappingContext.class));
		verify(activityRepository).save(activity);
	}

	@Test
	void updateActivity_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
		ArgumentCaptor<ActivityUpdateEvent> eventCaptor = ArgumentCaptor.forClass(ActivityUpdateEvent.class);

		activity.setId(activityId);
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class)).thenReturn(patchedDto);
		when(activityRepository.save(activity)).thenReturn(activity);
		when(activityRepository.findByIdWithMedia(activityId)).thenReturn(Optional.of(activity));

		activityService.updateActivity(activityId, patch);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(activity, eventCaptor.getValue().getActivity());
	}

	@Test
	void updateActivity_shouldThrowExceptionWhenActivityNotFound() {
		when(repositoryHelper.find(activityRepository, Activity.class, activityId))
			.thenThrow(RecordNotFoundException.of(Activity.class, activityId));

		assertThrows(RecordNotFoundException.class, () -> activityService.updateActivity(activityId, patch));

		verifyNoInteractions(activityMapper, jsonPatchService, validationService, eventPublisher);
		verify(activityRepository, never()).save(activity);
	}

	@Test
	void updateActivity_shouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class)).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> activityService.updateActivity(activityId, patch));

		verifyNoInteractions(validationService, eventPublisher);
		verify(activityRepository, never()).save(activity);
	}

	@Test
	void updateActivity_shouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		when(jsonPatchService.createFromPatch(patch, ActivityUpdateDto.class)).thenReturn(patchedDto);

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> activityService.updateActivity(activityId, patch));
		verify(validationService).validate(patchedDto);
		verifyNoInteractions(eventPublisher);
		verify(activityRepository, never()).save(activity);
	}

	@Test
	void deleteActivity_shouldDelete() {
		when(activityRepository.findByIdWithAssociations(activityId)).thenReturn(Optional.of(activity));

		activityService.deleteActivity(activityId);

		verify(activityRepository).delete(activity);
	}

	@Test
	void deleteActivity_shouldPublishEvent() {
		ArgumentCaptor<ActivityDeleteEvent> eventCaptor = ArgumentCaptor.forClass(ActivityDeleteEvent.class);

		when(activityRepository.findByIdWithAssociations(activityId)).thenReturn(Optional.of(activity));

		activityService.deleteActivity(activityId);

		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(activity, eventCaptor.getValue().getActivity());
	}

	@Test
	void deleteActivity_shouldThrowExceptionWhenActivityNotFound() {
		when(activityRepository.findByIdWithAssociations(activityId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> activityService.deleteActivity(activityId));

		verify(activityRepository, never()).delete(activity);
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	void calculateCaloriesBurned_shouldCalculateCaloriesForActivityWeightAlreadySaved() {
		user.setWeight(BigDecimal.valueOf(80));
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(repositoryHelper.find(userRepository, User.class, userId)).thenReturn(user);
		when(calculationsService.toCalculatedResponseDto(activity, BigDecimal.valueOf(80),
				calculateRequestDto.getTime()))
			.thenReturn(calculatedResponseDto);

		ActivityCalculatedResponseDto result = activityService.calculateCaloriesBurned(activityId, calculateRequestDto);

		verify(calculationsService).toCalculatedResponseDto(activity, BigDecimal.valueOf(80),
				calculateRequestDto.getTime());
		assertEquals(calculatedResponseDto, result);
	}

	@Test
	void calculateCaloriesBurned_shouldCalculateCaloriesForActivityWeightInRequest() {
		calculateRequestDto.setWeight(BigDecimal.valueOf(80));
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		when(calculationsService.toCalculatedResponseDto(activity, BigDecimal.valueOf(80),
				calculateRequestDto.getTime()))
			.thenReturn(calculatedResponseDto);

		ActivityCalculatedResponseDto result = activityService.calculateCaloriesBurned(activityId, calculateRequestDto);

		verify(calculationsService).toCalculatedResponseDto(activity, BigDecimal.valueOf(80),
				calculateRequestDto.getTime());
		assertEquals(calculatedResponseDto, result);
	}

	@Test
	void calculateCaloriesBurned_shouldCalculateCaloriesForActivityUsingWeightInRequestEvenIfAlreadySaved() {
		user.setWeight(BigDecimal.valueOf(70));
		calculateRequestDto.setWeight(BigDecimal.valueOf(80));
		when(repositoryHelper.find(activityRepository, Activity.class, activityId)).thenReturn(activity);
		when(calculationsService.toCalculatedResponseDto(activity, BigDecimal.valueOf(80),
				calculateRequestDto.getTime()))
			.thenReturn(calculatedResponseDto);

		ActivityCalculatedResponseDto result = activityService.calculateCaloriesBurned(activityId, calculateRequestDto);

		verify(calculationsService).toCalculatedResponseDto(activity, BigDecimal.valueOf(80),
				calculateRequestDto.getTime());
		assertEquals(calculatedResponseDto, result);
	}

	@Test
	void calculateCaloriesBurned_shouldNotProceedWhenActivityNotFound() {
		when(repositoryHelper.find(activityRepository, Activity.class, activityId))
			.thenThrow(RecordNotFoundException.of(Activity.class, activityId));

		assertThrows(RecordNotFoundException.class,
				() -> activityService.calculateCaloriesBurned(activityId, calculateRequestDto));

		verifyNoInteractions(calculationsService);
	}

	@Test
	void getActivity_shouldReturnActivityWhenFound() {
		when(activityRepository.findByIdWithMedia(activityId)).thenReturn(Optional.of(activity));
		when(activityMapper.toResponse(activity)).thenReturn(detailedResponseDto);

		ActivityResponseDto result = activityService.getActivity(activityId);

		assertEquals(detailedResponseDto, result);
		verify(activityRepository).findByIdWithMedia(activityId);
		verify(activityMapper).toResponse(activity);
		verify(activityPopulationService).populate(detailedResponseDto);
	}

	@Test
	void getActivity_shouldNotProceedWhenActivityNotFound() {
		when(activityRepository.findByIdWithMedia(activityId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> activityService.getActivity(activityId));
		verify(activityRepository).findByIdWithMedia(activityId);
		verifyNoInteractions(activityMapper);
	}

	@Test
	void getFilteredActivities_shouldReturnFilteredActivities() {
		Page<Activity> activityPage = new PageImpl<>(List.of(activity), pageable, 1);

		when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(activityPage);
		when(activityMapper.toSummary(activity)).thenReturn(responseDto);

		Page<ActivitySummaryDto> result = activityService.getFilteredActivities(filter, pageable);

		assertEquals(1, result.getTotalElements());
		assertSame(responseDto, result.getContent().get(0));
		verify(activityRepository).findAll(any(Specification.class), eq(pageable));
		verify(activityMapper).toSummary(activity);
	}

	@Test
	void getFilteredActivities_shouldReturnEmptyPageWhenFilterHasNoCriteria() {
		filter.setFilterCriteria(new ArrayList<>());
		Page<Activity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

		when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<ActivitySummaryDto> result = activityService.getFilteredActivities(filter, pageable);

		assertTrue(result.isEmpty());
		verify(activityRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void getFilteredActivities_shouldReturnEmptyPageWhenNoActivitiesMatchFilter() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("nonexistentKey");
		filter.setFilterCriteria(List.of(criteria));
		Page<Activity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

		when(activityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<ActivitySummaryDto> result = activityService.getFilteredActivities(filter, pageable);

		assertTrue(result.isEmpty());
		verify(activityRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void getAllActivityEntities_shouldReturnAllActivityEntities() {
		List<Activity> activities = List.of(activity);
		when(activityRepository.findAll()).thenReturn(activities);

		List<Activity> result = activityService.getAllActivityEntities();

		assertEquals(activities, result);
		verify(activityRepository).findAll();
	}

	@Test
	void getAllActivityEntities_shouldReturnEmptyListWhenNoActivities() {
		List<Activity> activities = List.of();
		when(activityRepository.findAll()).thenReturn(activities);

		List<Activity> result = activityService.getAllActivityEntities();

		assertTrue(result.isEmpty());
		verify(activityRepository).findAll();
	}

}

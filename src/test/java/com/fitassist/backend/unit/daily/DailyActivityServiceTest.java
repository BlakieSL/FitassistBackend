package com.fitassist.backend.unit.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.activity.DailyActivityItemCreateDto;
import com.fitassist.backend.dto.request.activity.DailyActivityItemUpdateDto;
import com.fitassist.backend.dto.response.activity.ActivityCalculatedResponseDto;
import com.fitassist.backend.dto.response.daily.DailyActivitiesResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.daily.DailyActivityMapper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.daily.DailyCart;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.ActivityRepository;
import com.fitassist.backend.repository.DailyCartActivityRepository;
import com.fitassist.backend.repository.DailyCartRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.daily.DailyActivityServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyActivityServiceTest {

	private static final int USER_ID = 1;

	private static final int ACTIVITY_ID = 1;

	private static final int INVALID_ACTIVITY_ID = 999;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private ValidationService validationService;

	@Mock
	private DailyActivityMapper dailyActivityMapper;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private DailyCartRepository dailyCartRepository;

	@Mock
	private DailyCartActivityRepository dailyCartActivityRepository;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CalculationsService calculationsService;

	@InjectMocks
	private DailyActivityServiceImpl dailyActivityService;

	private Activity activity;

	private DailyCart dailyCart;

	private DailyCartActivity dailyCartActivity;

	private DailyActivityItemCreateDto createDto;

	private JsonMergePatch patch;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		activity = new Activity();
		dailyCart = new DailyCart();
		dailyCartActivity = new DailyCartActivity();
		createDto = new DailyActivityItemCreateDto();
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);

		activity.setId(1);
		dailyCart.setId(1);
		dailyCartActivity.setId(1);
		dailyCartActivity.setTime((short) 30);
		createDto.setTime((short) 60);
		createDto.setWeight(BigDecimal.valueOf(75.00));
		createDto.setDate(LocalDate.now());

		User user = new User();
		user.setId(USER_ID);
		user.setWeight(BigDecimal.valueOf(70.00));
		dailyCart.setUser(user);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void addActivityToDailyActivityItem_shouldUpdateExistingDailyActivity() {
		dailyCart.getDailyCartActivities().add(dailyCartActivity);

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(dailyCartRepository.findByUserIdAndDate(eq(USER_ID), any())).thenReturn(Optional.of(dailyCart));
		when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID)).thenReturn(activity);

		when(dailyCartActivityRepository.findByDailyCartIdAndActivityId(dailyCart.getId(), ACTIVITY_ID))
			.thenReturn(Optional.of(dailyCartActivity));

		dailyActivityService.addActivityToDailyCart(ACTIVITY_ID, createDto);

		verify(dailyCartRepository).save(dailyCart);
		assertEquals((short) 90, dailyCart.getDailyCartActivities().get(0).getTime());
		assertEquals(dailyCart.getDailyCartActivities().get(0).getWeight(), createDto.getWeight());
	}

	@Test
	void addActivityToDailyActivityItem_shouldAddNewDailyActivity_whenNotFound() {
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(dailyCartRepository.findByUserIdAndDate(eq(USER_ID), any())).thenReturn(Optional.of(dailyCart));
		when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID)).thenReturn(activity);
		when(dailyCartActivityRepository.findByDailyCartIdAndActivityId(dailyCart.getId(), ACTIVITY_ID))
			.thenReturn(Optional.empty());

		dailyActivityService.addActivityToDailyCart(ACTIVITY_ID, createDto);

		verify(dailyCartRepository).save(dailyCart);
		assertEquals(dailyCart.getDailyCartActivities().get(0).getTime(), createDto.getTime());
	}

	@Test
	void addActivityToDailyActivityItem_shouldCreateNewDailyCart_whenNotFound() {
		User user = new User();
		user.setId(USER_ID);
		DailyCart newDailyActivity = DailyCart.createDate(user);
		newDailyActivity.setId(1);
		newDailyActivity.setUser(user);

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(dailyCartRepository.findByUserIdAndDate(eq(USER_ID), any())).thenReturn(Optional.empty());
		when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);
		when(dailyCartRepository.save(any(DailyCart.class))).thenReturn(newDailyActivity);
		when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID)).thenReturn(activity);
		when(dailyCartActivityRepository.findByDailyCartIdAndActivityId(anyInt(), eq(ACTIVITY_ID)))
			.thenReturn(Optional.empty());

		dailyActivityService.addActivityToDailyCart(ACTIVITY_ID, createDto);

		ArgumentCaptor<DailyCart> dailyActivityCaptor = ArgumentCaptor.forClass(DailyCart.class);
		verify(dailyCartRepository, times(2)).save(dailyActivityCaptor.capture());
		DailyCart savedDailyActivity = dailyActivityCaptor.getValue();
		assertEquals(USER_ID, savedDailyActivity.getUser().getId());
		assertEquals(createDto.getTime(), savedDailyActivity.getDailyCartActivities().get(0).getTime());
	}

	@Test
	void removeActivityFromDailyCart_shouldRemoveExistingDailyActivityFromDailyCart() {
		when(dailyCartActivityRepository.findByIdWithoutAssociations(ACTIVITY_ID))
			.thenReturn(Optional.of(dailyCartActivity));
		doNothing().when(dailyCartActivityRepository).delete(dailyCartActivity);

		dailyActivityService.removeActivityFromDailyCart(ACTIVITY_ID);

		verify(dailyCartActivityRepository).delete(dailyCartActivity);
	}

	@Test
	void removeActivityFromDailyCart_shouldThrowException_whenDailyActivityNotFound() {
		when(dailyCartActivityRepository.findByIdWithoutAssociations(ACTIVITY_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class,
				() -> dailyActivityService.removeActivityFromDailyCart(ACTIVITY_ID));
	}

	@Test
	void updateDailyActivityItem_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		DailyActivityItemUpdateDto patchedDto = new DailyActivityItemUpdateDto();
		patchedDto.setTime((short) 120);
		patchedDto.setWeight(BigDecimal.valueOf(80.00));

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(dailyCartActivityRepository.findByIdWithoutAssociations(ACTIVITY_ID))
			.thenReturn(Optional.of(dailyCartActivity));

		doReturn(patchedDto).when(jsonPatchService)
			.createFromPatch(any(JsonMergePatch.class), eq(DailyActivityItemUpdateDto.class));

		dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch);

		verify(validationService).validate(patchedDto);
		assertEquals(patchedDto.getTime(), dailyCartActivity.getTime());
		assertEquals(patchedDto.getWeight(), dailyCartActivity.getWeight());
		verify(dailyCartActivityRepository).save(dailyCartActivity);
	}

	@Test
	void updateDailyActivityItem_shouldThrowException_whenDailyActivityItemNotFound() {
		when(dailyCartActivityRepository.findByIdWithoutAssociations(ACTIVITY_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class,
				() -> dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));

		verifyNoInteractions(validationService);
		verify(dailyCartActivityRepository, never()).save(any());
	}

	@Test
	void updateDailyActivityItem_shouldThrowException_whenPatchFails()
			throws JsonPatchException, JsonProcessingException {
		when(dailyCartActivityRepository.findByIdWithoutAssociations(ACTIVITY_ID))
			.thenReturn(Optional.of(dailyCartActivity));
		doThrow(JsonPatchException.class).when(jsonPatchService)
			.createFromPatch(any(JsonMergePatch.class), eq(DailyActivityItemUpdateDto.class));

		assertThrows(JsonPatchException.class, () -> dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));

		verifyNoInteractions(validationService);
		verify(dailyCartActivityRepository, never()).save(any());
	}

	@Test
	void updateDailyActivityItem_shouldThrowException_whenPatchValidationFails()
			throws JsonPatchException, JsonProcessingException {
		DailyActivityItemUpdateDto patchedDto = new DailyActivityItemUpdateDto();
		patchedDto.setTime((short) 120);

		when(dailyCartActivityRepository.findByIdWithoutAssociations(ACTIVITY_ID))
			.thenReturn(Optional.of(dailyCartActivity));
		doReturn(patchedDto).when(jsonPatchService)
			.createFromPatch(any(JsonMergePatch.class), eq(DailyActivityItemUpdateDto.class));

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));

		verify(validationService).validate(patchedDto);
		verify(dailyCartActivityRepository, never()).save(any());
	}

	@Test
	void getActivitiesFromDailyCart_shouldReturnActivities() {
		User user = new User();
		user.setId(USER_ID);
		user.setWeight(BigDecimal.valueOf(70));
		dailyCart.setUser(user);
		dailyCartActivity.setWeight(BigDecimal.valueOf(75.00));
		dailyCart.getDailyCartActivities().add(dailyCartActivity);

		ActivityCalculatedResponseDto calculatedResponseDto = new ActivityCalculatedResponseDto();
		calculatedResponseDto.setCaloriesBurned(BigDecimal.valueOf(100));

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(dailyCartRepository.findByUserIdAndDateWithActivityAssociations(USER_ID, LocalDate.now()))
			.thenReturn(Optional.of(dailyCart));
		when(calculationsService.toCalculatedResponseDto(dailyCartActivity)).thenReturn(calculatedResponseDto);

		DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyCart(LocalDate.now());

		assertEquals(1, result.getActivities().size());
		assertEquals(BigDecimal.valueOf(100), result.getTotalCaloriesBurned());
	}

	@Test
	void getActivitiesFromDailyCart_shouldReturnEmptyActivities_whenDailyCartNotFound() {
		User user = new User();
		user.setId(USER_ID);
		user.setWeight(BigDecimal.valueOf(70));
		DailyCart newDailyActivity = DailyCart.createDate(user);
		newDailyActivity.setUser(user);

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
		when(dailyCartRepository.findByUserIdAndDateWithActivityAssociations(eq(USER_ID), any()))
			.thenReturn(Optional.empty());

		DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyCart(LocalDate.now());

		assertTrue(result.getActivities().isEmpty());
		assertEquals(BigDecimal.ZERO, result.getTotalCaloriesBurned());
		verify(dailyActivityMapper, never()).toActivityCalculatedResponseDto(any());
	}

}

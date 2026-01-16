package com.fitassist.backend.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.mapper.ActivityMapper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserActivity;
import com.fitassist.backend.repository.ActivityRepository;
import com.fitassist.backend.repository.UserActivityRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.activity.ActivityPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withoutType.UserActivityServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserActivityServiceTest {

	@Mock
	private UserActivityRepository userActivityRepository;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ActivityMapper activityMapper;

	@Mock
	private ActivityPopulationService activityPopulationService;

	private UserActivityServiceImpl userActivityService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	@BeforeEach
	void setUp() {
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		userActivityService = new UserActivityServiceImpl(userRepository, activityRepository, userActivityRepository,
				activityMapper, activityPopulationService);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		int userId = 1;
		int activityId = 100;
		User user = new User();
		Activity activity = new Activity();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

		userActivityService.saveToUser(activityId);

		verify(userActivityRepository).save(any(UserActivity.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		int userId = 1;
		int activityId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userActivityService.saveToUser(activityId));

		verify(userActivityRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		int userId = 1;
		int activityId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userActivityService.saveToUser(activityId));

		verify(userActivityRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfActivityNotFound() {
		int userId = 1;
		int activityId = 100;
		User user = new User();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userActivityService.saveToUser(activityId));

		verify(userActivityRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		int userId = 1;
		int activityId = 100;
		UserActivity userActivity = UserActivity.of(new User(), new Activity());

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userActivityRepository.findByUserIdAndActivityId(userId, activityId))
			.thenReturn(Optional.of(userActivity));

		userActivityService.deleteFromUser(activityId);

		verify(userActivityRepository).delete(userActivity);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserActivityNotFound() {
		int userId = 1;
		int activityId = 100;
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userActivityRepository.findByUserIdAndActivityId(userId, activityId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userActivityService.deleteFromUser(activityId));

		verify(userActivityRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnPagedActivities() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Activity activity1 = new Activity();
		activity1.setId(1);
		activity1.setMediaList(new ArrayList<>());

		Activity activity2 = new Activity();
		activity2.setId(2);
		activity2.setMediaList(new ArrayList<>());

		UserActivity ua1 = UserActivity.of(new User(), activity1);
		UserActivity ua2 = UserActivity.of(new User(), activity2);

		ActivitySummaryDto dto1 = new ActivitySummaryDto();
		dto1.setId(1);
		ActivitySummaryDto dto2 = new ActivitySummaryDto();
		dto2.setId(2);

		Page<UserActivity> userActivityPage = new PageImpl<>(List.of(ua1, ua2), pageable, 2);

		when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), eq(pageable))).thenReturn(userActivityPage);
		when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
		when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userActivityService.getAllFromUser(userId, pageable);

		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		verify(activityMapper, times(2)).toSummaryDto(any(Activity.class));
		verify(activityPopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyPageIfNoActivities() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserActivity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), eq(pageable))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userActivityService.getAllFromUser(userId, pageable);

		assertTrue(result.isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}

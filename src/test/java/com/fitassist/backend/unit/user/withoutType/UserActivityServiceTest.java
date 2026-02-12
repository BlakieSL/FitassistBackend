package com.fitassist.backend.unit.user.withoutType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.activity.ActivityMapper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.UserActivity;
import com.fitassist.backend.repository.ActivityRepository;
import com.fitassist.backend.repository.UserActivityRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.activity.ActivityPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withoutType.UserActivityImplService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserActivityServiceTest {

	private static final int USER_ID = 1;

	private static final int ACTIVITY_ID = 100;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private UserActivityRepository userActivityRepository;

	@Mock
	private ActivityMapper activityMapper;

	@Mock
	private ActivityPopulationService activityPopulationService;

	private UserActivityImplService userActivityService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	private User user;

	private Activity activity;

	private UserActivity userActivity;

	@BeforeEach
	void setUp() {
		userActivityService = new UserActivityImplService(userRepository, activityRepository, userActivityRepository,
				activityMapper, activityPopulationService);
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		activity = new Activity();
		activity.setId(ACTIVITY_ID);
		activity.setMediaList(new ArrayList<>());
		userActivity = UserActivity.of(user, activity);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		when(userActivityRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(activityRepository.findById(ACTIVITY_ID)).thenReturn(Optional.of(activity));

		userActivityService.saveToUser(ACTIVITY_ID);

		verify(userActivityRepository).save(any(UserActivity.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		when(userActivityRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userActivityService.saveToUser(ACTIVITY_ID));

		verify(userActivityRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		when(userActivityRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userActivityService.saveToUser(ACTIVITY_ID));

		verify(userActivityRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfActivityNotFound() {
		when(userActivityRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(activityRepository.findById(ACTIVITY_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userActivityService.saveToUser(ACTIVITY_ID));

		verify(userActivityRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		when(userActivityRepository.findByUserIdAndActivityId(USER_ID, ACTIVITY_ID))
			.thenReturn(Optional.of(userActivity));

		userActivityService.deleteFromUser(ACTIVITY_ID);

		verify(userActivityRepository).delete(userActivity);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserActivityNotFound() {
		when(userActivityRepository.findByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userActivityService.deleteFromUser(ACTIVITY_ID));

		verify(userActivityRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnPagedActivities() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Activity activity2 = new Activity();
		activity2.setId(2);
		activity2.setMediaList(new ArrayList<>());
		UserActivity ua2 = UserActivity.of(user, activity2);

		ActivitySummaryDto dto1 = new ActivitySummaryDto();
		dto1.setId(ACTIVITY_ID);
		ActivitySummaryDto dto2 = new ActivitySummaryDto();
		dto2.setId(2);

		Page<UserActivity> userActivityPage = new PageImpl<>(List.of(userActivity, ua2), pageable, 2);

		when(userActivityRepository.findAllByUserIdWithMedia(eq(USER_ID), eq(pageable))).thenReturn(userActivityPage);
		when(activityMapper.toSummary(activity)).thenReturn(dto1);
		when(activityMapper.toSummary(activity2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userActivityService.getAllFromUser(USER_ID, pageable);

		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		verify(activityMapper, times(2)).toSummary(any(Activity.class));
		verify(activityPopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyPageIfNoActivities() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserActivity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userActivityRepository.findAllByUserIdWithMedia(eq(USER_ID), eq(pageable))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userActivityService.getAllFromUser(USER_ID, pageable);

		assertTrue(result.isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}

package source.code.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.user.User;
import source.code.model.user.UserActivity;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withoutType.UserActivityServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private UserActivityServiceImpl userActivityService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
        userActivityService = new UserActivityServiceImpl(
                userRepository,
                activityRepository,
                userActivityRepository,
                activityMapper
        );
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("saveToUser - Should save to user")
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int activityId = 100;
        User user = new User();
        Activity activity = new Activity();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        userActivityService.saveToUser(activityId);

        verify(userActivityRepository).save(any(UserActivity.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int activityId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userActivityService.saveToUser(activityId));

        verify(userActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int activityId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.saveToUser(activityId));

        verify(userActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if activity not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfActivityNotFound() {
        int userId = 1;
        int activityId = 100;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityId(userId, activityId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.saveToUser(activityId));

        verify(userActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
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
    @DisplayName("deleteFromUser - Should throw exception if user activity not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserActivityNotFound() {
        int userId = 1;
        int activityId = 100;
        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findByUserIdAndActivityId(userId, activityId))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.deleteFromUser(activityId));

        verify(userActivityRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all activities by type")
    public void getAllFromUser_ShouldReturnAllActivitiesByType() {
        int userId = 1;
        UserActivity activity1 = UserActivity.of(new User(), new Activity());
        UserActivity activity2 = UserActivity.of(new User(), new Activity());
        ActivityResponseDto dto1 = new ActivityResponseDto();
        ActivityResponseDto dto2 = new ActivityResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findAllByUserId(userId))
                .thenReturn(List.of(activity1, activity2));
        when(activityMapper.toResponseDto(activity1.getActivity())).thenReturn(dto1);
        when(activityMapper.toResponseDto(activity2.getActivity())).thenReturn(dto2);

        var result = userActivityService.getAllFromUser();

        assertEquals(2, result.size());
        assertTrue(result.contains((BaseUserEntity) dto1));
        assertTrue(result.contains((BaseUserEntity) dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no activities")
    public void getAllFromUser_ShouldReturnEmptyListIfNoActivities() {
        int userId = 1;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findAllByUserId(userId))
                .thenReturn(List.of());

        var result = userActivityService.getAllFromUser();

        assertTrue(result.isEmpty());
        verify(activityMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int activityId = 100;
        long likeCount = 0L;
        long saveCount = 10;
        Activity activity = new Activity();

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(userActivityRepository.countByActivityId(activityId))
                .thenReturn(likeCount);
        when(userActivityRepository.countByActivityId(activityId))
                .thenReturn(saveCount);

        var result = userActivityService.calculateLikesAndSaves(activityId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(activityRepository).findById(activityId);
        verify(userActivityRepository).countByActivityId(activityId);
        verify(userActivityRepository).countByActivityId(activityId);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if activity not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfActivityNotFound() {
        int activityId = 100;

        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.calculateLikesAndSaves(activityId));

        verify(userActivityRepository, never()).countByActivityId(anyInt());
    }
}
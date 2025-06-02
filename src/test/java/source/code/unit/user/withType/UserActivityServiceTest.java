package source.code.unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import source.code.model.user.UserActivity;
import source.code.model.user.profile.User;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withType.UserActivityServiceImpl;

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
    @InjectMocks
    private UserActivityServiceImpl userActivityService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("saveToUser - Should save to user with type")
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int activityId = 100;
        short type = 1;
        User user = new User();
        Activity activity = new Activity();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityIdAndType(userId, activityId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        userActivityService.saveToUser(activityId, type);

        verify(userActivityRepository).save(any(UserActivity.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int activityId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityIdAndType(userId, activityId, type))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userActivityService.saveToUser(activityId, type));

        verify(userActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int activityId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityIdAndType(userId, activityId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.saveToUser(activityId, type));

        verify(userActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if activity not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfActivityNotFound() {
        int userId = 1;
        int activityId = 100;
        short type = 1;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.existsByUserIdAndActivityIdAndType(userId, activityId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.saveToUser(activityId, type));

        verify(userActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int activityId = 100;
        short type = 1;
        UserActivity userActivity = UserActivity.of(new User(), new Activity(), type);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findByUserIdAndActivityIdAndType(userId, activityId, type))
                .thenReturn(Optional.of(userActivity));

        userActivityService.deleteFromUser(activityId, type);

        verify(userActivityRepository).delete(userActivity);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if user activity not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserActivityNotFound() {
        int userId = 1;
        int activityId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findByUserIdAndActivityIdAndType(userId, activityId, type))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.deleteFromUser(activityId, type));

        verify(userActivityRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all activities by type")
    public void getAllFromUser_ShouldReturnAllActivitiesByType() {
        int userId = 1;
        short type = 1;
        UserActivity activity1 = UserActivity.of(new User(), new Activity(), type);
        UserActivity activity2 = UserActivity.of(new User(), new Activity(), type);
        ActivityResponseDto dto1 = new ActivityResponseDto();
        ActivityResponseDto dto2 = new ActivityResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of(activity1, activity2));
        when(activityMapper.toResponseDto(activity1.getActivity())).thenReturn(dto1);
        when(activityMapper.toResponseDto(activity2.getActivity())).thenReturn(dto2);

        var result = userActivityService.getAllFromUser(type);

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
        when(userActivityRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userActivityService.getAllFromUser(type);

        assertTrue(result.isEmpty());
        verify(activityMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int activityId = 100;
        long saveCount = 5;
        long likeCount = 10;
        Activity activity = new Activity();

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(userActivityRepository.countByActivityIdAndType(activityId, (short) 1))
                .thenReturn(saveCount);
        when(userActivityRepository.countByActivityIdAndType(activityId, (short) 2))
                .thenReturn(likeCount);

        var result = userActivityService.calculateLikesAndSaves(activityId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(activityRepository).findById(activityId);
        verify(userActivityRepository).countByActivityIdAndType(activityId, (short) 1);
        verify(userActivityRepository).countByActivityIdAndType(activityId, (short) 2);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if activity not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfActivityNotFound() {
        int activityId = 100;

        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userActivityService.calculateLikesAndSaves(activityId));

        verify(userActivityRepository, never()).countByActivityIdAndType(anyInt(), anyShort());
    }
}
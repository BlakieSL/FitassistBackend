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
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.activity.ActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserActivity;
import source.code.repository.ActivityRepository;
import source.code.repository.MediaRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.user.interaction.withoutType.UserActivityServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private AwsS3Service awsS3Service;
    private UserActivityServiceImpl userActivityService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
        userActivityService = new UserActivityServiceImpl(
                userRepository,
                activityRepository,
                userActivityRepository,
                activityMapper,
                mediaRepository,
                awsS3Service
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
        ActivityResponseDto dto1 = new ActivityResponseDto();
        dto1.setId(1);
        dto1.setImageName("activity1.jpg");
        ActivityResponseDto dto2 = new ActivityResponseDto();
        dto2.setId(2);
        dto2.setImageName("activity2.jpg");

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(List.of(dto1, dto2));
        when(awsS3Service.getImage("activity1.jpg")).thenReturn("https://s3.../activity1.jpg");
        when(awsS3Service.getImage("activity2.jpg")).thenReturn("https://s3.../activity2.jpg");

        var result = userActivityService.getAllFromUser(userId, "DESC");

        assertEquals(2, result.size());
        verify(awsS3Service, times(2)).getImage(anyString());
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no activities")
    public void getAllFromUser_ShouldReturnEmptyListIfNoActivities() {
        int userId = 1;

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(List.of());

        var result = userActivityService.getAllFromUser(userId, "DESC");

        assertTrue(result.isEmpty());
        verify(awsS3Service, never()).getImage(anyString());
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

    @Test
    @DisplayName("getAllFromUser with sortDirection DESC - Should sort by interaction date DESC")
    public void getAllFromUser_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ActivityResponseDto dto1 = createActivityResponseDto(1, older);
        ActivityResponseDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, "DESC");

        assertSortedResult(result, 2, 2, 1);
        verify(userActivityRepository).findActivityDtosByUserId(userId);
    }

    @Test
    @DisplayName("getAllFromUser with sortDirection ASC - Should sort by interaction date ASC")
    public void getAllFromUser_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ActivityResponseDto dto1 = createActivityResponseDto(1, older);
        ActivityResponseDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(new ArrayList<>(List.of(dto2, dto1)));

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, "ASC");

        assertSortedResult(result, 2, 1, 2);
        verify(userActivityRepository).findActivityDtosByUserId(userId);
    }

    @Test
    @DisplayName("getAllFromUser default - Should sort DESC when no direction specified")
    public void getAllFromUser_DefaultShouldSortDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ActivityResponseDto dto1 = createActivityResponseDto(1, older);
        ActivityResponseDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, "DESC");

        assertSortedResult(result, 2, 2, 1);
        verify(userActivityRepository).findActivityDtosByUserId(userId);
    }

    @Test
    @DisplayName("getAllFromUser - Should handle null dates properly")
    public void getAllFromUser_ShouldHandleNullDates() {
        int userId = 1;

        ActivityResponseDto dto1 = createActivityResponseDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        ActivityResponseDto dto2 = createActivityResponseDto(2, null);
        ActivityResponseDto dto3 = createActivityResponseDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2, dto3)));

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, "DESC");

        assertSortedResult(result, 3, 3, 1, 2);
        verify(userActivityRepository).findActivityDtosByUserId(userId);
    }

    @Test
    @DisplayName("getAllFromUser - Should populate image URLs after sorting")
    public void getAllFromUser_ShouldPopulateImageUrlsAfterSorting() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        ActivityResponseDto dto1 = createActivityResponseDto(1, older);
        dto1.setImageName("image1.jpg");
        ActivityResponseDto dto2 = createActivityResponseDto(2, newer);
        dto2.setImageName("image2.jpg");

        when(userActivityRepository.findActivityDtosByUserId(userId))
                .thenReturn(new ArrayList<>(List.of(dto1, dto2)));
        when(awsS3Service.getImage("image1.jpg")).thenReturn("https://s3.com/image1.jpg");
        when(awsS3Service.getImage("image2.jpg")).thenReturn("https://s3.com/image2.jpg");

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, "DESC");

        assertNotNull(result);
        assertEquals(2, result.size());
        ActivityResponseDto first = (ActivityResponseDto) result.get(0);
        ActivityResponseDto second = (ActivityResponseDto) result.get(1);
        assertEquals("https://s3.com/image2.jpg", first.getFirstImageUrl());
        assertEquals("https://s3.com/image1.jpg", second.getFirstImageUrl());
        verify(awsS3Service).getImage("image1.jpg");
        verify(awsS3Service).getImage("image2.jpg");
    }

    private ActivityResponseDto createActivityResponseDto(int id, LocalDateTime interactionDate) {
        ActivityResponseDto dto = new ActivityResponseDto();
        dto.setId(id);
        dto.setUserActivityInteractionCreatedAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((ActivityResponseDto) result.get(i)).getId());
        }
    }
}
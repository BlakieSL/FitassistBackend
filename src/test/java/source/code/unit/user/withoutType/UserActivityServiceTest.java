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
import org.springframework.data.domain.Sort;
import source.code.dto.response.activity.ActivitySummaryDto;
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
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.implementation.user.interaction.withoutType.UserActivityServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    private ImageUrlPopulationService imagePopulationService;
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
                imagePopulationService
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

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(List.of(ua1, ua2));
        when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
        when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);

        var result = userActivityService.getAllFromUser(userId, Sort.Direction.DESC);

        assertEquals(2, result.size());
        verify(activityMapper, times(2)).toSummaryDto(any(Activity.class));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no activities")
    public void getAllFromUser_ShouldReturnEmptyListIfNoActivities() {
        int userId = 1;

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        var result = userActivityService.getAllFromUser(userId, Sort.Direction.DESC);

        assertTrue(result.isEmpty());
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

        Activity activity1 = new Activity();
        activity1.setId(1);
        activity1.setMediaList(new ArrayList<>());

        Activity activity2 = new Activity();
        activity2.setId(2);
        activity2.setMediaList(new ArrayList<>());

        UserActivity ua1 = UserActivity.of(new User(), activity1);
        ua1.setCreatedAt(older);
        UserActivity ua2 = UserActivity.of(new User(), activity2);
        ua2.setCreatedAt(newer);

        ActivitySummaryDto dto1 = createActivityResponseDto(1, older);
        ActivitySummaryDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ua2, ua1)));
        when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
        when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
    }

    @Test
    @DisplayName("getAllFromUser with sortDirection ASC - Should sort by interaction date ASC")
    public void getAllFromUser_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Activity activity1 = new Activity();
        activity1.setId(1);
        activity1.setMediaList(new ArrayList<>());

        Activity activity2 = new Activity();
        activity2.setId(2);
        activity2.setMediaList(new ArrayList<>());

        UserActivity ua1 = UserActivity.of(new User(), activity1);
        ua1.setCreatedAt(older);
        UserActivity ua2 = UserActivity.of(new User(), activity2);
        ua2.setCreatedAt(newer);

        ActivitySummaryDto dto1 = createActivityResponseDto(1, older);
        ActivitySummaryDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ua1, ua2)));
        when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
        when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, Sort.Direction.ASC);

        assertSortedResult(result, 2, 1, 2);
    }

    @Test
    @DisplayName("getAllFromUser default - Should sort DESC when no direction specified")
    public void getAllFromUser_DefaultShouldSortDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Activity activity1 = new Activity();
        activity1.setId(1);
        activity1.setMediaList(new ArrayList<>());

        Activity activity2 = new Activity();
        activity2.setId(2);
        activity2.setMediaList(new ArrayList<>());

        UserActivity ua1 = UserActivity.of(new User(), activity1);
        ua1.setCreatedAt(older);
        UserActivity ua2 = UserActivity.of(new User(), activity2);
        ua2.setCreatedAt(newer);

        ActivitySummaryDto dto1 = createActivityResponseDto(1, older);
        ActivitySummaryDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ua2, ua1)));
        when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
        when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
    }

    @Test
    @DisplayName("getAllFromUser - Should handle null dates properly")
    public void getAllFromUser_ShouldHandleNullDates() {
        int userId = 1;

        Activity activity1 = new Activity();
        activity1.setId(1);
        activity1.setMediaList(new ArrayList<>());

        Activity activity2 = new Activity();
        activity2.setId(2);
        activity2.setMediaList(new ArrayList<>());

        Activity activity3 = new Activity();
        activity3.setId(3);
        activity3.setMediaList(new ArrayList<>());

        UserActivity ua1 = UserActivity.of(new User(), activity1);
        ua1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        UserActivity ua2 = UserActivity.of(new User(), activity2);
        ua2.setCreatedAt(null);
        UserActivity ua3 = UserActivity.of(new User(), activity3);
        ua3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

        ActivitySummaryDto dto1 = createActivityResponseDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        ActivitySummaryDto dto2 = createActivityResponseDto(2, null);
        ActivitySummaryDto dto3 = createActivityResponseDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ua3, ua1, ua2)));
        when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
        when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);
        when(activityMapper.toSummaryDto(activity3)).thenReturn(dto3);

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 3, 3, 1, 2);
    }

    @Test
    @DisplayName("getAllFromUser - Should populate image URLs after sorting")
    public void getAllFromUser_ShouldPopulateImageUrlsAfterSorting() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        source.code.model.media.Media media1 = new source.code.model.media.Media();
        media1.setImageName("image1.jpg");
        source.code.model.media.Media media2 = new source.code.model.media.Media();
        media2.setImageName("image2.jpg");

        Activity activity1 = new Activity();
        activity1.setId(1);
        activity1.setMediaList(List.of(media1));

        Activity activity2 = new Activity();
        activity2.setId(2);
        activity2.setMediaList(List.of(media2));

        UserActivity ua1 = UserActivity.of(new User(), activity1);
        ua1.setCreatedAt(older);
        UserActivity ua2 = UserActivity.of(new User(), activity2);
        ua2.setCreatedAt(newer);

        ActivitySummaryDto dto1 = createActivityResponseDto(1, older);
        ActivitySummaryDto dto2 = createActivityResponseDto(2, newer);

        when(userActivityRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ua2, ua1)));
        when(activityMapper.toSummaryDto(activity1)).thenReturn(dto1);
        when(activityMapper.toSummaryDto(activity2)).thenReturn(dto2);

        List<BaseUserEntity> result = userActivityService.getAllFromUser(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        // Image URL population is handled by imagePopulationService internally
        verify(activityMapper).toSummaryDto(activity1);
        verify(activityMapper).toSummaryDto(activity2);
    }

    private ActivitySummaryDto createActivityResponseDto(int id, LocalDateTime interactionDate) {
        ActivitySummaryDto dto = new ActivitySummaryDto();
        dto.setId(id);
        dto.setUserActivityInteractionCreatedAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((ActivitySummaryDto) result.get(i)).getId());
        }
    }
}

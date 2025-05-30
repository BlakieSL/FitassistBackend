package unit.daily;

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
import source.code.dto.request.activity.DailyActivityItemCreateDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyActivityMapper;
import source.code.model.activity.Activity;
import source.code.model.activity.DailyActivity;
import source.code.model.activity.DailyActivityItem;
import source.code.model.user.profile.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityItemRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.daily.DailyActivityServiceImpl;

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
    private DailyActivityRepository dailyActivityRepository;
    @Mock
    private DailyActivityItemRepository dailyActivityItemRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DailyActivityServiceImpl dailyActivityService;

    private Activity activity;
    private DailyActivity dailyActivity;
    private DailyActivityItem dailyActivityItem;
    private DailyActivityItemCreateDto createDto;
    private JsonMergePatch patch;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        activity = new Activity();
        dailyActivity = new DailyActivity();
        dailyActivityItem = new DailyActivityItem();
        createDto = new DailyActivityItemCreateDto();
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);

        activity.setId(1);
        dailyActivity.setId(1);
        dailyActivityItem.setId(1);
        createDto.setTime(60);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void addActivityToDailyActivityItem_shouldUpdateExistingDailyActivityItemTime() {
        dailyActivity.getDailyActivityItems().add(dailyActivityItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID))
                .thenReturn(activity);
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));

        dailyActivityService.addActivityToDailyActivityItem(ACTIVITY_ID, createDto);

        verify(dailyActivityRepository).save(dailyActivity);
        assertEquals(dailyActivity.getDailyActivityItems().get(0).getTime(), createDto.getTime());
    }

    @Test
    void addActivityToDailyActivityItem_shouldAddNewDailyActivityItem_whenNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID))
                .thenReturn(activity);
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.empty());

        dailyActivityService.addActivityToDailyActivityItem(ACTIVITY_ID, createDto);

        verify(dailyActivityRepository).save(dailyActivity);
        assertEquals(dailyActivity.getDailyActivityItems().get(0).getTime(), createDto.getTime());
    }

    @Test
    void addActivityToDailyActivityItem_shouldCreateNewDailyActivity_whenNotFound() {
        User user = new User();
        user.setId(USER_ID);
        DailyActivity newDailyActivity = DailyActivity.createForToday(user);
        newDailyActivity.setId(1);
        newDailyActivity.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());
        when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);
        when(dailyActivityRepository.save(any(DailyActivity.class)))
                .thenReturn(newDailyActivity);
        when(repositoryHelper.find(activityRepository, Activity.class, ACTIVITY_ID))
                .thenReturn(activity);
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                anyInt(),
                eq(ACTIVITY_ID))
        ).thenReturn(Optional.empty());

        dailyActivityService.addActivityToDailyActivityItem(ACTIVITY_ID, createDto);

        ArgumentCaptor<DailyActivity> dailyActivityCaptor = ArgumentCaptor
                .forClass(DailyActivity.class);
        verify(dailyActivityRepository, times(2))
                .save(dailyActivityCaptor.capture());
        DailyActivity savedDailyActivity = dailyActivityCaptor.getValue();
        assertEquals(USER_ID, savedDailyActivity.getUser().getId());
        assertEquals(
                createDto.getTime(),
                savedDailyActivity.getDailyActivityItems().get(0).getTime()
        );
    }

    @Test
    void removeActivityFromDailyActivityItem_shouldRemoveExistingDailyActivityItem() {
        dailyActivity.getDailyActivityItems().add(dailyActivityItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));

        dailyActivityService.removeActivityFromDailyActivityItem(ACTIVITY_ID);

        verify(dailyActivityRepository).save(dailyActivity);
        assertTrue(dailyActivity.getDailyActivityItems().isEmpty());
    }

    @Test
    void removeActivityFromDailyActivityItem_shouldThrowException_whenDailyActivityItemNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.removeActivityFromDailyActivityItem(ACTIVITY_ID)
        );
    }

    @Test
    void removeActivityFromDailyActivityItem_shouldThrowException_whenDailyActivityNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.removeActivityFromDailyActivityItem(ACTIVITY_ID)
        );
    }

    @Test
    void updateDailyActivityItem_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        DailyActivityItemCreateDto patchedDto = new DailyActivityItemCreateDto();
        patchedDto.setTime(120);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));
        doReturn(patchedDto).when(jsonPatchService).applyPatch(
                any(JsonMergePatch.class),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)
        );

        dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch);

        verify(validationService).validate(patchedDto);
        assertEquals(patchedDto.getTime(), dailyActivityItem.getTime());
        verify(dailyActivityRepository).save(dailyActivity);
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenDailyActivityItemNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenDailyActivityNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch));
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));
        doThrow(JsonPatchException.class).when(jsonPatchService).applyPatch(
                any(JsonMergePatch.class),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)
        );

        assertThrows(JsonPatchException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch)
        );

        verifyNoInteractions(validationService);
        verify(dailyActivityRepository, never()).save(dailyActivity);
    }
    @Test
    void updateDailyActivityItem_shouldThrowException_whenPatchValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        DailyActivityItemCreateDto patchedDto = new DailyActivityItemCreateDto();
        patchedDto.setTime(120);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityItemRepository.findByDailyActivityIdAndActivityId(
                dailyActivity.getId(),
                ACTIVITY_ID)
        ).thenReturn(Optional.of(dailyActivityItem));
        doReturn(patchedDto).when(jsonPatchService).applyPatch(
                any(JsonMergePatch.class),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)
        );

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () ->
                dailyActivityService.updateDailyActivityItem(ACTIVITY_ID, patch)
        );

        verify(validationService).validate(patchedDto);
        verify(dailyActivityRepository, never()).save(dailyActivity);
    }

    @Test
    void getActivitiesFromDailyActivity_shouldReturnActivities() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(70);
        dailyActivity.setUser(user);
        dailyActivity.getDailyActivityItems().add(dailyActivityItem);

        ActivityCalculatedResponseDto calculatedResponseDto = new ActivityCalculatedResponseDto();
        calculatedResponseDto.setCaloriesBurned(100);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyActivity));
        when(dailyActivityMapper.toActivityCalculatedResponseDto(
                dailyActivityItem,
                user.getWeight())
        ).thenReturn(calculatedResponseDto);

        DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyActivity();

        assertEquals(1, result.getActivities().size());
        assertEquals(100, result.getTotalCaloriesBurned());
    }

    @Test
    void getActivitiesFromDailyActivity_shouldReturnEmptyActivities_whenDailyActivityNotFound() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(70);
        DailyActivity newDailyActivity = DailyActivity.createForToday(user);
        newDailyActivity.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyActivityRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());
        when(dailyActivityRepository.save(any(DailyActivity.class))).thenReturn(newDailyActivity);

        DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyActivity();

        assertTrue(result.getActivities().isEmpty());
        assertEquals(0, result.getTotalCaloriesBurned());
    }
}

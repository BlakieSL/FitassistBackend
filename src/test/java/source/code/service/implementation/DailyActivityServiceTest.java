package source.code.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.DailyActivityItemCreateDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.DailyActivitiesResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.mapper.DailyActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.Activity.ActivityCategory;
import source.code.model.Activity.DailyActivity;
import source.code.model.Activity.DailyActivityItem;
import source.code.model.User.User;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.Acitivity.DailyActivityServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyActivityServiceTest {
    @Mock private DailyActivityMapper dailyActivityMapper;
    @Mock
    private JsonPatchHelper jsonPatchHelper;
    @Mock
    private DailyActivityRepository dailyActivityRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DailyActivityServiceImpl dailyActivityService;

    private User user1;
    private User user2;
    private ActivityCategory activityCategory1;
    private ActivityCategory activityCategory2;
    private Activity activity1;
    private Activity activity2;
    private DailyActivity dailyActivity1;
    private DailyActivity dailyActivity2;
    private DailyActivityItem dailyActivityItem1;
    private DailyActivityItem dailyActivityItem2;
    private DailyActivityItemCreateDto createDto;

      @BeforeEach
    void setup() {
        user1 = createUser(1);
        user2 = createUser(2);

        activityCategory1 = createActivityCategory(1, "Name1");
        activityCategory2 = createActivityCategory(2, "Name2");

        activity1 = createActivity(1);
        activity1.setActivityCategory(activityCategory1);

        activity2 = createActivity(2);
        activity2.setActivityCategory(activityCategory2);

        dailyActivity1 = createDailyActivity(1, user1);
        dailyActivity2 = createDailyActivity(2, user2);

        dailyActivityItem1 = createDailyActivityItem(1, activity1, dailyActivity1);
        dailyActivityItem2 = createDailyActivityItem(2, activity2, dailyActivity2);

        dailyActivity1.getDailyActivityItems().add(dailyActivityItem1);
        dailyActivity2.getDailyActivityItems().add(dailyActivityItem2);

        createDto = createDailyActivityItemCreateDto(30);
    }

    private User createUser(int id) {
       return User.createWithId(id);
    }

    private ActivityCategory createActivityCategory(int id, String name) {
        return ActivityCategory.createWithIdName(id, name);
    }

    private Activity createActivity(int id) {
        return Activity.createWithId(id);
    }

    private DailyActivity createDailyActivity(int id, User user) {
        return DailyActivity.createWithIdUser(id, user);
    }

    private DailyActivityItem createDailyActivityItem(int id,
                                                      Activity activity,
                                                      DailyActivity dailyActivity) {

        return DailyActivityItem.createWithIdActivityDailyActivity(id, activity, dailyActivity);
    }

    private DailyActivityItemCreateDto createDailyActivityItemCreateDto(int time) {
        return new DailyActivityItemCreateDto(time);
    }

    private void clearDailyActivity(DailyActivity dailyActivity) {
        dailyActivity.getDailyActivityItems().clear();
    }
    @Test
    void resetDailyCarts_shouldUpdateActivities_whenActivitiesExist() {
        //Arrange
        LocalDate today = LocalDate.now();
        dailyActivity1.setDate(today);
        dailyActivity1.getDailyActivityItems().clear();

        dailyActivity2.setDate(today);
        dailyActivity2.getDailyActivityItems().clear();

        when(dailyActivityRepository.findAll()).thenReturn(List.of(dailyActivity1, dailyActivity2));

        // Act
        dailyActivityService.resetDailyCarts();

        // Assert
        verify(dailyActivityRepository, times(1)).findAll();
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        verify(dailyActivityRepository, times(1)).save(dailyActivity2);
        assertEquals(today, dailyActivity1.getDate());
        assertEquals(today, dailyActivity2.getDate());
        assertTrue(dailyActivity1.getDailyActivityItems().isEmpty());
        assertTrue(dailyActivity2.getDailyActivityItems().isEmpty());
    }

    @Test
    void resetDailyCarts_shouldDoNothing_whenNoActivitiesExist() {
        // Arrange
        when(dailyActivityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        dailyActivityService.resetDailyCarts();

        // Assert
        verify(dailyActivityRepository, times(1)).findAll();
        verify(dailyActivityRepository, never()).save(any(DailyActivity.class));
    }

    @Test
    void addActivityToDailyActivityItem_shouldAddActivity_whenActivityDoesNotExistInDailyCart(){
        // Arrange
        int userId = user1.getId();
        int activityId = activity1.getId();
        when(dailyActivityRepository.findByUserId(userId)).thenReturn(Optional.of(dailyActivity1));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity1));

        // Act
        dailyActivityService.addActivityToDailyActivityItem(userId, activityId, createDto);

        // Assert
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertEquals(1, dailyActivity1.getDailyActivityItems().size());

        DailyActivityItem addedActivity = dailyActivity1.getDailyActivityItems().get(0);
        assertEquals(activity1, addedActivity.getActivity());
        assertEquals(createDto.getTime(), addedActivity.getTime());
        assertEquals(dailyActivity1, addedActivity.getDailyActivity());
    }

    @Test
    void addActivityToDailyActivityItem_shouldUpdateTime_whenActivityExistsInDailyCart() {
        // Arrange
        int userId = user1.getId();
        int activityId = activity1.getId();
        int existingTime = 15;
        dailyActivityItem1.setTime(existingTime);

        when(dailyActivityRepository.findByUserId(userId)).thenReturn(Optional.of(dailyActivity1));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity1));

        // Act
        dailyActivityService.addActivityToDailyActivityItem(userId, activityId, createDto);

        // Assert
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertEquals(1, dailyActivity1.getDailyActivityItems().size());

        DailyActivityItem updatedActivity = dailyActivity1.getDailyActivityItems().get(0);
        assertEquals(activity1, updatedActivity.getActivity());
        assertEquals(createDto.getTime(), updatedActivity.getTime());
        assertEquals(dailyActivity1, updatedActivity.getDailyActivity());
    }


    @Test
    void addActivityToDailyActivityItem_shouldThrowException_whenActivityNotFound() {
        // Arrange
        int userId = user1.getId();
        int activityId = activity1.getId();
        clearDailyActivity(dailyActivity1);
        when(dailyActivityRepository.findByUserId(userId)).thenReturn(Optional.of(dailyActivity1));
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.addActivityToDailyActivityItem(userId, activityId, createDto));

        assertEquals("Activity with id: " + activityId + " not found", exception.getMessage());
        verify(activityRepository, times(1)).findById(activityId);
        verify(dailyActivityRepository, never()).save(any());
    }

    @Test
    void removeActivityFromDailyActivity_shouldRemove_whenDailyActivityItemFound() {
        // Arrange
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));

        // Act
        dailyActivityService.removeActivityFromDailyActivity(user1.getId(), activity1.getId());

        // Assert
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertTrue(dailyActivity1.getDailyActivityItems().isEmpty());
    }

    @Test
    void removeActivityFromDailyActivity_shouldThrowException_whenDailyActivityItemNotFound() {
        // Arrange
        int nonExistentActivityId = 11;
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.removeActivityFromDailyActivity(user1.getId(), nonExistentActivityId));

        assertEquals("Activity with id: " + nonExistentActivityId + " not found", exception.getMessage());
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(dailyActivityRepository, never()).save(any());
    }

    @Test
    void updateDailyActivityItem_shouldUpdate_whenPatched() throws JsonPatchException, JsonProcessingException {
        // Arrange
        int newTime = 30;
        dailyActivityItem1.setTime(20);

        DailyActivityItemCreateDto patchedDto = new DailyActivityItemCreateDto();
        patchedDto.setTime(newTime);

        JsonMergePatch patch = mock(JsonMergePatch.class);
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));
        when(jsonPatchHelper.applyPatch(
                eq(patch),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class)))
                .thenReturn(patchedDto);

        // Act
        dailyActivityService.updateDailyActivityItem(user1.getId(), activity1.getId(), patch);

        // Assert
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(jsonPatchHelper, times(1)).applyPatch(
                eq(patch),
                any(DailyActivityItemCreateDto.class),
                eq(DailyActivityItemCreateDto.class));
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        assertEquals(newTime, dailyActivityItem1.getTime());
    }

    @Test
    void updateDailyActivityItem_shouldThrowException_whenDailyActivityItemNotFound()
            throws JsonPatchException, JsonProcessingException {
        //Arrange
        clearDailyActivity(dailyActivity1);
        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.updateDailyActivityItem(user1.getId(), activity1.getId(), mock(JsonMergePatch.class)));

        assertEquals("Activity with id: " + activity1.getId() + " not found in daily cart", exception.getMessage());
        verify(jsonPatchHelper, never()).applyPatch(any(),any(),any());
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
    }

    @Test
    void createNewDailyActivityForUser_shouldCreate_whenUserFound() {
        // Arrange
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(dailyActivityRepository.save(any(DailyActivity.class))).thenReturn(dailyActivity1);

        // Act
        DailyActivity createdDailyActivity = dailyActivityService.createNewDailyActivityForUser(user1.getId());

        // Assert
        assertNotNull(createdDailyActivity);
        assertEquals(user1.getId(), createdDailyActivity.getUser().getId());
        verify(userRepository, times(1)).findById(user1.getId());
        verify(dailyActivityRepository, times(1)).save(any(DailyActivity.class));
    }

    @Test
    void createNewDailyActivityFromDailyActivityItem_shouldThrowException_whenUserNotFound() {
        // Arrange
        int nonExistingUserId = 99;
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception  = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.createNewDailyActivityForUser(nonExistingUserId));

        assertEquals("User with id: " + nonExistingUserId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistingUserId);
        verify(dailyActivityRepository, never()).save(any(DailyActivity.class));
    }

    @Test
    void getActivitiesFromDailyActivityItem_shouldGetDailyActivitiesResponseDto_whenUserAndActivitiesFound() {
        // Arrange
        ActivityCalculatedResponseDto activityCalculatedDto = ActivityCalculatedResponseDto
                .createWithId(activity1.getId(), dailyActivityItem1.getTime());


        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));
        when(dailyActivityMapper.toActivityCalculatedResponseDto(dailyActivityItem1, user1.getWeight())).thenReturn(activityCalculatedDto);

        // Act
        DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyActivityItem(user1.getId());

        // Assert
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(dailyActivityMapper, times(1)).toActivityCalculatedResponseDto(dailyActivityItem1, user1.getWeight());
        assertEquals(1, result.getActivities().size());

        ActivityCalculatedResponseDto resultActivity = result.getActivities().get(0);
        assertEquals(activityCalculatedDto.getId(), resultActivity.getId());
        assertEquals(activityCalculatedDto.getTime(), resultActivity.getTime());
      }

    @Test
    void getActivitiesFromDailyActivityItem_shouldGetZeroTotalCaloriesBurnedAndEmptyActivities_whenNoActivitiesFound() {
        // Arrange
        dailyActivity1.getDailyActivityItems().clear();

        when(dailyActivityRepository.findByUserId(user1.getId())).thenReturn(Optional.of(dailyActivity1));

        // Act
        DailyActivitiesResponseDto result = dailyActivityService.getActivitiesFromDailyActivityItem(user1.getId());

        // Assert
        verify(dailyActivityRepository, times(1)).findByUserId(user1.getId());
        verify(dailyActivityMapper, never()).toActivityCalculatedResponseDto(any(), anyDouble());
        assertEquals(0, result.getTotalCaloriesBurned());
        assertTrue(result.getActivities().isEmpty());

    }
}

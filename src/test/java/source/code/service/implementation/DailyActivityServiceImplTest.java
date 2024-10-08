package source.code.service.implementation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.DailyCartActivityCreateDto;
import source.code.helper.CalculationsHelper;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.model.*;
import source.code.repository.ActivityRepository;
import source.code.repository.DailyActivityRepository;
import source.code.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyActivityServiceImplTest {
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private CalculationsHelper calculationsHelper;
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

    @Test
    void updateDailyCarts_shouldUpdateActivities_whenActivitiesExist() {
        // Arrange
        LocalDate today = LocalDate.now();
        DailyActivity dailyActivity1 = new DailyActivity();
        dailyActivity1.setId(1);
        dailyActivity1.setDate(today.minusDays(1));
        dailyActivity1.getDailyCartActivities().add(new DailyCartActivity());

        DailyActivity dailyActivity2 = new DailyActivity();
        dailyActivity2.setId(2);
        dailyActivity2.setDate(today.minusDays(2));
        dailyActivity2.getDailyCartActivities().add(new DailyCartActivity());

        List<DailyActivity> dailyActivities = List.of(dailyActivity1, dailyActivity2);

        when(dailyActivityRepository.findAll()).thenReturn(dailyActivities);

        // Act
        dailyActivityService.updateDailyCarts();

        // Assert
        verify(dailyActivityRepository, times(1)).findAll();
        verify(dailyActivityRepository, times(1)).save(dailyActivity1);
        verify(dailyActivityRepository, times(1)).save(dailyActivity2);
        assertEquals(today, dailyActivity1.getDate());
        assertEquals(today, dailyActivity2.getDate());
        assertTrue(dailyActivity1.getDailyCartActivities().isEmpty());
        assertTrue(dailyActivity2.getDailyCartActivities().isEmpty());
    }

    @Test
    void updateDailyCarts_shouldDoNothing_whenNoActivitiesExist() {
        // Arrange
        when(dailyActivityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        dailyActivityService.updateDailyCarts();

        // Assert
        verify(dailyActivityRepository, times(1)).findAll();
        verify(dailyActivityRepository, never()).save(any(DailyActivity.class));
    }

    @Test
    void addActivityToDailyCartActivity_shouldAddActivityToDailyCartActivity_whenActivityDoesNotExistInDailyCart() {
        // Arrange
        int userId = 1;
        int activityId = 1;
        int time = 30;
        DailyCartActivityCreateDto dto = new DailyCartActivityCreateDto(time);

        User user = new User();
        user.setId(userId);

        DailyActivity dailyActivity = new DailyActivity();
        dailyActivity.setUser(user);
        dailyActivity.getDailyCartActivities().clear();

        Activity activity = new Activity();
        activity.setId(activityId);

        when(dailyActivityRepository.findByUserId(userId)).thenReturn(Optional.of(dailyActivity));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        // Act
        dailyActivityService.addActivityToDailyCartActivity(userId, activityId, dto);

        // Assert
        verify(validationHelper, times(1)).validate(dto);
        verify(dailyActivityRepository, times(1)).save(dailyActivity);
        assertEquals(1, dailyActivity.getDailyCartActivities().size());

        DailyCartActivity addedActivity = dailyActivity.getDailyCartActivities().get(0);
        assertEquals(activity, addedActivity.getActivity());
        assertEquals(time, addedActivity.getTime());
        assertEquals(dailyActivity, addedActivity.getDailyCartActivity());
    }

    @Test
    void addActivityToDailyCartActivity_shouldUpdateTime_whenActivityExistsInDailyCart() {
        // Arrange
        int userId = 1;
        int activityId = 1;
        int existingTime = 15;
        int newTime = 30;
        DailyCartActivityCreateDto dto = new DailyCartActivityCreateDto(newTime);

        User user = new User();
        user.setId(userId);

        Activity activity = new Activity();
        activity.setId(activityId);

        DailyCartActivity existingDailyCartActivity = new DailyCartActivity();
        existingDailyCartActivity.setActivity(activity);
        existingDailyCartActivity.setTime(existingTime);

        DailyActivity dailyActivity = new DailyActivity();
        dailyActivity.setUser(user);
        dailyActivity.getDailyCartActivities().add(existingDailyCartActivity);

        existingDailyCartActivity.setDailyCartActivity(dailyActivity);

        when(dailyActivityRepository.findByUserId(userId)).thenReturn(Optional.of(dailyActivity));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        // Act
        dailyActivityService.addActivityToDailyCartActivity(userId, activityId, dto);

        // Assert
        verify(validationHelper, times(1)).validate(dto);
        verify(dailyActivityRepository, times(1)).save(dailyActivity);
        assertEquals(1, dailyActivity.getDailyCartActivities().size());

        DailyCartActivity updatedActivity = dailyActivity.getDailyCartActivities().get(0);
        assertEquals(activity, updatedActivity.getActivity());
        assertEquals(newTime, updatedActivity.getTime());
        assertEquals(dailyActivity, updatedActivity.getDailyCartActivity());
    }

    @Test
    void addActivityToDailyCartActivity_shouldThrowException_whenValidationFails() {
        // Arrange
        int userId = 1;
        int activityId = 1;
        int time = 30;
        DailyCartActivityCreateDto dto = new DailyCartActivityCreateDto(time);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationHelper).validate(dto);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                dailyActivityService.addActivityToDailyCartActivity(userId, activityId, dto)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(validationHelper, times(1)).validate(dto);
        verify(dailyActivityRepository, never()).save(any());
    }


    @Test
    void addActivityToDailyCartActivity_shouldThrowException_whenActivityNotFound() {
        // Arrange
        int userId = 1;
        int activityId = 1;
        int time = 30;
        DailyCartActivityCreateDto dto = new DailyCartActivityCreateDto(time);

        User user = new User();
        user.setId(userId);

        DailyActivity dailyActivity = new DailyActivity();
        dailyActivity.setUser(user);
        dailyActivity.getDailyCartActivities().clear();

        when(dailyActivityRepository.findByUserId(userId)).thenReturn(Optional.of(dailyActivity));
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                dailyActivityService.addActivityToDailyCartActivity(userId, activityId, dto)
        );

        assertEquals("Activity with id: " + activityId + " not found", exception.getMessage());
        verify(validationHelper, times(1)).validate(dto);
        verify(activityRepository, times(1)).findById(activityId);
        verify(dailyActivityRepository, never()).save(any());
    }

}

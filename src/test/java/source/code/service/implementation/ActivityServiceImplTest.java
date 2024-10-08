package source.code.service.implementation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.ActivityCreateDto;
import source.code.dto.request.CalculateActivityCaloriesRequestDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.helper.CalculationsHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.ActivityMapper;
import source.code.model.Activity;
import source.code.model.ActivityCategory;
import source.code.model.Role;
import source.code.model.User;
import source.code.repository.ActivityCategoryRepository;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceImplTest {
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private CalculationsHelper calculationsHelper;
    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ActivityCategoryRepository activityCategoryRepository;
    @Mock
    private UserActivityRepository userActivityRepository;
    @InjectMocks
    private ActivityServiceImpl activityService;

    @Test
    void createActivity_shouldReturnActivityResponseDto_whenValidationPassed() {
        // Arrange
        ActivityCreateDto dto = new ActivityCreateDto();
        Activity activity = new Activity();
        ActivityResponseDto responseDto = new ActivityResponseDto();

        doNothing().when(validationHelper).validate(dto);
        when(activityMapper.toEntity(dto)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        // Act
        ActivityResponseDto result = activityService.createActivity(dto);
        // Assert
        verify(validationHelper).validate(dto);
        verify(activityRepository).save(activity);
        verify(activityMapper).toEntity(dto);
        verify(activityMapper).toResponseDto(activity);
    }

    @Test
    void createActivity_shouldThrowException_whenValidationFails() {
        // Arrange
        ActivityCreateDto dto = new ActivityCreateDto();

        doThrow(new IllegalArgumentException("Invalid activity data")).when(validationHelper).validate(dto);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityService.createActivity(dto));

        assertEquals("Invalid activity data", exception.getMessage());
        verify(validationHelper, times(1)).validate(dto);
        verifyNoInteractions(activityRepository, activityMapper);
    }

    @Test
    void getActivity_shouldReturnActivityResponse_whenActivityFound() {
        //Arrange
        int activityId = 1;
        Activity activity = new Activity();
        ActivityResponseDto responseDto = new ActivityResponseDto();

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMapper.toResponseDto(activity)).thenReturn(responseDto);

        //Act
        ActivityResponseDto result = activityService.getActivity(activityId);

        //Assert
        verify(activityRepository, times(1)).findById(activityId);
        verify(activityMapper, times(1)).toResponseDto(activity);
    }

    @Test
    void getActivity_shouldNotMap_whenActivityNotFound() {
        // Arrange
        int activityId = 1;

        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> activityService.getActivity(activityId));

        System.out.println(exception.getMessage());
        assertEquals("Activity with id: " + activityId + " not found", exception.getMessage());
        verify(activityRepository, times(1)).findById(activityId);
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAllActivities_shouldReturnAllActivities_whenActivitiesFound() {
        ActivityCategory activityCategory = new ActivityCategory(1, "testName", "testIcon", "testGradient");
        Activity activity1 = new Activity(1, "Running", 1.1, activityCategory);
        Activity activity2 = new Activity(2, "Walking", 1.2, activityCategory);
        List<Activity> activities = List.of(activity1, activity2);

        ActivityResponseDto dto1 = new ActivityResponseDto(1, "Running", 1.1, "testName", 1);
        ActivityResponseDto dto2 = new ActivityResponseDto(2, "Walking", 1.2, "testName", 1);
        List<ActivityResponseDto> expectedDtos = List.of(dto1, dto2);

        when(activityRepository.findAll()).thenReturn(activities);
        when(activityMapper.toResponseDto(activity1)).thenReturn(dto1);
        when(activityMapper.toResponseDto(activity2)).thenReturn(dto2);

        // Act
        List<ActivityResponseDto> result = activityService.getAllActivities();

        // Assert
        assertEquals(expectedDtos, result);
        verify(activityRepository, times(1)).findAll();
        verify(activityMapper, times(1)).toResponseDto(activity1);
        verify(activityMapper, times(1)).toResponseDto(activity2);
    }

    @Test
    void getAllActivities_shouldReturnEmptyList_whenActivityNotFound() {
        when(activityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ActivityResponseDto> result = activityService.getAllActivities();

        // Assert
        assertTrue(result.isEmpty());
        verify(activityRepository, times(1)).findAll();
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAllCategories_shouldReturnAllCategories_whenCategoriesFound() {
        // Arrange
        ActivityCategory category1 = new ActivityCategory(1, "Cardio", "icon1", "gradient1");
        ActivityCategory category2 = new ActivityCategory(2, "Strength", "icon2", "gradient2");
        List<ActivityCategory> categories = List.of(category1, category2);

        ActivityCategoryResponseDto dto1 = new ActivityCategoryResponseDto(1, "Cardio", "icon1", "gradient1");
        ActivityCategoryResponseDto dto2 = new ActivityCategoryResponseDto(2, "Strength", "icon2", "gradient2");
        List<ActivityCategoryResponseDto> expectedDtos = List.of(dto1, dto2);

        when(activityCategoryRepository.findAll()).thenReturn(categories);
        when(activityMapper.toCategoryDto(category1)).thenReturn(dto1);
        when(activityMapper.toCategoryDto(category2)).thenReturn(dto2);

        // Act
        List<ActivityCategoryResponseDto> result = activityService.getAllCategories();

        // Assert
        assertEquals(expectedDtos, result);
        verify(activityCategoryRepository, times(1)).findAll();
        verify(activityMapper, times(1)).toCategoryDto(category1);
        verify(activityMapper, times(1)).toCategoryDto(category2);
    }

    @Test
    void getAllCategories_shouldReturnEmptyList_whenCategoryNotFound() {
        when(activityCategoryRepository.findAll()).thenReturn(Collections.emptyList());

        //Act
        List<ActivityCategoryResponseDto> result = activityService.getAllCategories();

        //Assert
        assertTrue(result.isEmpty());
        verify(activityCategoryRepository, times(1)).findAll();
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getActivitiesByCategory_shouldReturnActivities_whenActivitiesFound() {
        // Arrange
        int categoryId = 1;
        ActivityCategory activityCategory = new ActivityCategory(categoryId, "CategoryName", "icon", "gradient");
        Activity activity1 = new Activity(1, "Running", 1.1, activityCategory);
        Activity activity2 = new Activity(2, "Walking", 1.2, activityCategory);
        List<Activity> activities = List.of(activity1, activity2);

        ActivityResponseDto dto1 = new ActivityResponseDto(1, "Running", 1.1, "CategoryName", categoryId);
        ActivityResponseDto dto2 = new ActivityResponseDto(2, "Walking", 1.2, "CategoryName", categoryId);
        List<ActivityResponseDto> expectedDtos = List.of(dto1, dto2);

        when(activityRepository.findAllByActivityCategory_Id(categoryId)).thenReturn(activities);
        when(activityMapper.toResponseDto(activity1)).thenReturn(dto1);
        when(activityMapper.toResponseDto(activity2)).thenReturn(dto2);

        // Act
        List<ActivityResponseDto> result = activityService.getActivitiesByCategory(categoryId);

        // Assert
        assertEquals(expectedDtos, result);
        verify(activityRepository, times(1)).findAllByActivityCategory_Id(categoryId);
        verify(activityMapper, times(1)).toResponseDto(activity1);
        verify(activityMapper, times(1)).toResponseDto(activity2);
    }

    @Test
    void getActivitiesByCategory_shouldReturnEmptyList_whenNoActivitiesFound() {
        // Arrange
        int categoryId = 1;
        when(activityRepository.findAllByActivityCategory_Id(categoryId)).thenReturn(Collections.emptyList());

        // Act
        List<ActivityResponseDto> result = activityService.getActivitiesByCategory(categoryId);

        // Assert
        assertTrue(result.isEmpty());
        verify(activityRepository, times(1)).findAllByActivityCategory_Id(categoryId);
        verifyNoInteractions(activityMapper);
    }

    @Test
    void calculateCaloriesBurned_shouldReturnCalculatedResponse_whenValidationPassed() {
        // Arrange
        int activityCategoryId = 1;
        String activityCategoryName = "Running";
        ActivityCategory activityCategory = new ActivityCategory(activityCategoryId, activityCategoryName, "icon", "gradient");

        int activityId = 1;
        String activityName = "Running";
        double met = 1.1;

        int userId = 1;
        int time = 30;

        int caloriesBurned = 50;

        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto(userId, time);
        User user = new User(
                1,
                "John",
                "Doe",
                "john.doe@example.com",
                "StrongPassword123",
                "Male",
                LocalDate.of(1990, 1, 1),
                175.0,
                70.0,
                2000.0,
                "Lose weight",
                "Moderate",
                null,
                null);
        Activity activity = new Activity(activityId, activityName, met, activityCategory);
        ActivityCalculatedResponseDto expectedResponse = new ActivityCalculatedResponseDto(
                activityId,
                activityName,
                met,
                activityCategoryName,
                activityCategoryId,
                caloriesBurned,
                time);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityMapper.toCalculatedDto(activity, user, time)).thenReturn(expectedResponse);

        // Act
        ActivityCalculatedResponseDto result = activityService.calculateCaloriesBurned(activityId, request);

        // Assert
        assertEquals(expectedResponse, result);
        verify(validationHelper, times(1)).validate(request);
        verify(userRepository, times(1)).findById(userId);
        verify(activityRepository, times(1)).findById(activityId);
        verify(activityMapper, times(1)).toCalculatedDto(activity, user, time);
    }

    @Test
    void calculateCaloriesBurned_shouldThrowException_whenValidationFails() {
        // Arrange
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto(1, 30);

        doThrow(new IllegalArgumentException("Invalid request")).when(validationHelper).validate(request);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityService.calculateCaloriesBurned(1, request));

        assertEquals("Invalid request", exception.getMessage());
        verify(validationHelper, times(1)).validate(request);
        verifyNoInteractions(userRepository, activityRepository, activityMapper);
    }

}

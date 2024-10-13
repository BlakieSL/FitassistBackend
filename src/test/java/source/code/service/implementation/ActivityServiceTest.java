package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.ActivityCreateDto;
import source.code.dto.request.CalculateActivityCaloriesRequestDto;
import source.code.dto.request.SearchRequestDto;
import source.code.dto.response.ActivityAverageMetResponseDto;
import source.code.dto.response.ActivityCalculatedResponseDto;
import source.code.dto.response.ActivityCategoryResponseDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.mapper.ActivityMapper;
import source.code.model.Activity.Activity;
import source.code.model.Activity.ActivityCategory;
import source.code.model.User.User;
import source.code.model.User.UserActivity;
import source.code.repository.ActivityCategoryRepository;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.Acitivity.ActivityServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {
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
    private ActivityCategory activityCategory1;
    private ActivityCategory activityCategory2;
    private ActivityCategoryResponseDto activityCategoryResponseDto1;
    private ActivityCategoryResponseDto activityCategoryResponseDto2;
    private Activity activity1;
    private Activity activity2;
    private ActivityCreateDto createDto1;
    private ActivityCreateDto createDto2;
    private ActivityResponseDto responseDto1;
    private ActivityResponseDto responseDto2;
    private User user1;
    private User user2;
    private UserActivity userActivity1;
    private UserActivity userActivity2;
    @InjectMocks
    private ActivityServiceImpl activityService;
    @BeforeEach
    void setup() {
        activityCategory1 = createActivityCategory(1, "ActivityCategoryName1");
        activityCategory2 = createActivityCategory(2, "ActivityCategoryName2");

        activityCategoryResponseDto1 = createActivityCategoryResponseDto(activityCategory1.getId(), activityCategory1.getName());
        activityCategoryResponseDto2 = createActivityCategoryResponseDto(activityCategory2.getId(), activityCategory2.getName());

        activity1 = createActivity(1, "ActivityName1", 1.1);
        activity1.setActivityCategory(activityCategory1);

        activity2 = createActivity(2, "ActivityName2", 2.2);
        activity2.setActivityCategory(activityCategory2);

        createDto1 = createActivityCreateDto(activity1.getName(), activity1.getMet(), activity1.getActivityCategory().getId());
        createDto2 = createActivityCreateDto(activity2.getName(), activity2.getMet(), activity2.getActivityCategory().getId());

        responseDto1 = createActivityResponseDto(activity1.getId(), activity1.getActivityCategory().getName(), activity1.getActivityCategory().getId());
        responseDto2 = createActivityResponseDto(activity2.getId(), activity2.getActivityCategory().getName(), activity2.getActivityCategory().getId());

        user1 = createUser(1);
        user2 = createUser(2);

        userActivity1 = createUserActivity(1, user1, activity1, (short)1);
        userActivity2 = createUserActivity(2, user2, activity2, (short)2);
    }

    private ActivityCategory createActivityCategory(int id, String name) {
        return ActivityCategory.createWithIdName(id, name);
    }

    private ActivityCategoryResponseDto createActivityCategoryResponseDto(int id, String name){
        return ActivityCategoryResponseDto.createWithIdName(id, name);
    }

    private Activity createActivity(int id, String name, double met) {
        return Activity.createWithIdNameMet(id, name, met);
    }

    private ActivityCreateDto createActivityCreateDto(String name, double met, int categoryId ) {
        return new ActivityCreateDto(name, met, categoryId);
    }

    private ActivityResponseDto createActivityResponseDto(int id, String categoryName, int categoryId) {
        return ActivityResponseDto.createWithIdCategoryNameCategoryId(id, categoryName, categoryId);
    }

    private User createUser(int id) {
        return User.createWithId(id);
    }

    private UserActivity createUserActivity(int id, User user, Activity activity, short type) {
        return new UserActivity(id, user, activity, type);
    }

    @Test
    void createActivity_shouldCreate() {
        // Arrange
        when(activityMapper.toEntity(createDto1)).thenReturn(activity1);
        when(activityRepository.save(activity1)).thenReturn(activity1);
        when(activityMapper.toResponseDto(activity1)).thenReturn(responseDto1);

        // Act
        ActivityResponseDto result = activityService.createActivity(createDto1);

        // Assert
        verify(activityRepository, times(1)).save(activity1);
        verify(activityMapper, times(1)).toEntity(createDto1);
        verify(activityMapper, times(1)).toResponseDto(activity1);

        assertNotNull(result);
        assertEquals(responseDto1.getId(), result.getId());
        assertEquals(responseDto1.getCategoryName(), result.getCategoryName());
        assertEquals(responseDto1.getCategoryId(), result.getCategoryId());
    }

    @Test
    void getActivity_shouldReturnActivityResponse_whenActivityFound() {
        //Arrange
        int activityId = activity1.getId();
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity1));
        when(activityMapper.toResponseDto(activity1)).thenReturn(responseDto1);

        //Act
        ActivityResponseDto result = activityService.getActivity(activityId);

        //Assert
        verify(activityRepository, times(1)).findById(activityId);
        verify(activityMapper, times(1)).toResponseDto(activity1);
        assertEquals(responseDto1, result);
        assertEquals(responseDto1.getId(), result.getId());
        assertEquals(responseDto1.getCategoryName(), result.getCategoryName());
        assertEquals(responseDto1.getCategoryId(), result.getCategoryId());
    }

    @Test
    void getActivity_shouldNotMap_whenActivityNotFound() {
        // Arrange
        int activityId = activity1.getId();
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> activityService.getActivity(activityId));

        assertEquals("Activity with id: " + activityId + " not found", exception.getMessage());
        verify(activityRepository, times(1)).findById(activityId);
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAllActivities_shouldReturnAllActivities_whenActivitiesFound() {
        // Assert
        List<Activity> activities = List.of(activity1, activity2);
        List<ActivityResponseDto> expectedDtos = List.of(responseDto1, responseDto2);

        when(activityRepository.findAll()).thenReturn(activities);
        when(activityMapper.toResponseDto(activity1)).thenReturn(responseDto1);
        when(activityMapper.toResponseDto(activity2)).thenReturn(responseDto2);

        // Act
        List<ActivityResponseDto> result = activityService.getAllActivities();

        // Assert
        verify(activityRepository, times(1)).findAll();
        verify(activityMapper, times(1)).toResponseDto(activity1);
        verify(activityMapper, times(1)).toResponseDto(activity2);
        assertEquals(expectedDtos, result);
        assertEquals(expectedDtos.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtos.get(1).getId(), result.get(1).getId());
    }

    @Test
    void getAllActivities_shouldReturnEmptyList_whenActivityNotFound() {
        // Arrange
        when(activityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ActivityResponseDto> result = activityService.getAllActivities();

        // Assert
        verify(activityRepository, times(1)).findAll();
        verifyNoInteractions(activityMapper);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCategories_shouldReturnAllCategories_whenCategoriesFound() {
        // Arrange
        List<ActivityCategory> categories = List.of(activityCategory1, activityCategory2);
        List<ActivityCategoryResponseDto> expectedDtos = List.of(activityCategoryResponseDto1, activityCategoryResponseDto2);

        when(activityCategoryRepository.findAll()).thenReturn(categories);
        when(activityMapper.toCategoryDto(activityCategory1)).thenReturn(activityCategoryResponseDto1);
        when(activityMapper.toCategoryDto(activityCategory2)).thenReturn(activityCategoryResponseDto2);

        // Act
        List<ActivityCategoryResponseDto> result = activityService.getAllCategories();

        // Assert
        verify(activityCategoryRepository, times(1)).findAll();
        verify(activityMapper, times(1)).toCategoryDto(activityCategory1);
        verify(activityMapper, times(1)).toCategoryDto(activityCategory2);
        assertEquals(expectedDtos, result);
        assertEquals(expectedDtos.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtos.get(1).getId(), result.get(1).getId());
    }

    @Test
    void getAllCategories_shouldReturnEmptyList_whenCategoryNotFound() {
        // Arrange
        when(activityCategoryRepository.findAll()).thenReturn(Collections.emptyList());

        //Act
        List<ActivityCategoryResponseDto> result = activityService.getAllCategories();

        //Assert
        verify(activityCategoryRepository, times(1)).findAll();
        verifyNoInteractions(activityMapper);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActivitiesByCategory_shouldReturnActivities_whenActivitiesFound() {
        // Arrange
        int categoryId = activityCategory1.getId();
        List<Activity> activities = List.of(activity1);
        List<ActivityResponseDto> expectedDtos = List.of(responseDto1);

        when(activityRepository.findAllByActivityCategory_Id(categoryId)).thenReturn(activities);
        when(activityMapper.toResponseDto(activity1)).thenReturn(responseDto1);

        // Act
        List<ActivityResponseDto> result = activityService.getActivitiesByCategory(categoryId);

        // Assert
        verify(activityRepository, times(1)).findAllByActivityCategory_Id(categoryId);
        verify(activityMapper, times(1)).toResponseDto(activity1);
        assertEquals(expectedDtos, result);
        assertEquals(expectedDtos.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getActivitiesByCategory_shouldReturnEmptyList_whenNoActivitiesFound() {
        // Arrange
        int categoryId = 100;
        when(activityRepository.findAllByActivityCategory_Id(categoryId)).thenReturn(Collections.emptyList());

        // Act
        List<ActivityResponseDto> result = activityService.getActivitiesByCategory(categoryId);

        // Assert
        verify(activityRepository, times(1)).findAllByActivityCategory_Id(categoryId);
        verifyNoInteractions(activityMapper);
        assertTrue(result.isEmpty());
    }

    @Test
    void calculateCaloriesBurned_shouldCalculateCalories() {
        // Arrange
        int userId = user1.getId();
        int activityId = activity1.getId();
        int randomTime = 30;
        int randomCaloriesBurned = 50;

        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto(userId, randomTime);
        ActivityCalculatedResponseDto expectedResponse = new ActivityCalculatedResponseDto(
                activity1.getId(),
                activity1.getName(),
                activity1.getMet(),
                activity1.getActivityCategory().getName(),
                activity1.getActivityCategory().getId(),
                randomCaloriesBurned,
                randomTime);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity1));
        when(activityMapper.toCalculatedDto(activity1, user1, randomTime)).thenReturn(expectedResponse);

        // Act
        ActivityCalculatedResponseDto result = activityService.calculateCaloriesBurned(activityId, request);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(activityRepository, times(1)).findById(activityId);
        verify(activityMapper, times(1)).toCalculatedDto(activity1, user1, randomTime);
        assertEquals(expectedResponse, result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getCaloriesBurned(), result.getCaloriesBurned());
        assertEquals(expectedResponse.getTime(), result.getTime());
    }

    @Test
    void calculateCaloriesBurned_shouldThrowException_whenUserNotFound() {
        // Arrange
        int activityId = activity1.getId();
        int userId = user1.getId();
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto(userId, 30);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> activityService.calculateCaloriesBurned(activityId, request));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(activityRepository, activityMapper);
        assertEquals("User with id: " + userId + " not found", exception.getMessage());
    }

    @Test
    void calculateCaloriesBurned_shouldThrowException_whenActivityNotFound() {
        // Arrange
        int activityId = activity1.getId();
        int userId = user1.getId();
        CalculateActivityCaloriesRequestDto request = new CalculateActivityCaloriesRequestDto(userId, 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> activityService.calculateCaloriesBurned(activityId, request));

        verify(userRepository, times(1)).findById(userId);
        verify(activityRepository, times(1)).findById(activityId);
        verifyNoInteractions(activityMapper);
        assertEquals("Activity with id: " + activityId + " not found", exception.getMessage());
    }

    @Test
    void searchActivities_shouldReturnActivityResponseDto_whenActivitiesFound() {
        String searchName = "run";
        SearchRequestDto request = new SearchRequestDto(searchName);

        List<Activity> activities = List.of(activity1, activity2);
        List<ActivityResponseDto> expectedDtos = List.of(responseDto1, responseDto2);

        when(activityRepository.findAllByNameContainingIgnoreCase(searchName)).thenReturn(activities);
        when(activityMapper.toResponseDto(activity1)).thenReturn(responseDto1);
        when(activityMapper.toResponseDto(activity2)).thenReturn(responseDto2);

        // Act
        List<ActivityResponseDto> result = activityService.searchActivities(request);

        // Assert
        verify(activityRepository, times(1)).findAllByNameContainingIgnoreCase(searchName);
        verify(activityMapper, times(1)).toResponseDto(activity1);
        verify(activityMapper, times(1)).toResponseDto(activity2);
        assertEquals(expectedDtos, result);
        assertEquals(expectedDtos.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtos.get(1).getId(), result.get(1).getId());
    }

    @Test
    void searchActivities_shouldReturnEmptyList_whenActivitiesNotFound() {
        // Assert
        String searchName = "nonexistent";
        SearchRequestDto request = new SearchRequestDto(searchName);

        when(activityRepository.findAllByNameContainingIgnoreCase(searchName)).thenReturn(Collections.emptyList());

        // Act
        List<ActivityResponseDto> result = activityService.searchActivities(request);

        // Assert
        verify(activityRepository, times(1)).findAllByNameContainingIgnoreCase(searchName);
        verifyNoInteractions(activityMapper);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActivitiesByUser_shouldReturnActivities_whenActivitiesFound() {
        // Arrange
        int userId = user1.getId();

        List<UserActivity> userActivities = List.of(userActivity1, userActivity2);
        List<ActivityResponseDto> expectedDtos = List.of(responseDto1, responseDto2);

        when(userActivityRepository.findByUserId(userId)).thenReturn(userActivities);
        when(activityMapper.toResponseDto(userActivity1.getActivity())).thenReturn(responseDto1);
        when(activityMapper.toResponseDto(userActivity2.getActivity())).thenReturn(responseDto2);

        // Act
        List<ActivityResponseDto> result = activityService.getActivitiesByUser(userId);

        // Assert
        assertEquals(expectedDtos, result);
        verify(userActivityRepository, times(1)).findByUserId(userId);
        verify(activityMapper, times(1)).toResponseDto(userActivity1.getActivity());
        verify(activityMapper, times(1)).toResponseDto(userActivity2.getActivity());
    }

    @Test
    void getActivitiesByUser_shouldReturnEmptyList_whenActivitiesNotFound() {
        // Arrange
        int userId = user1.getId();

        when(userActivityRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<ActivityResponseDto> result = activityService.getActivitiesByUser(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userActivityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(activityMapper);
    }

    @Test
    void getAverageMet_shouldReturnActivityAverageMetResponseDto_whenActivitiesFound() {
        int categoryId = activityCategory1.getId();

        List<Activity> activities = List.of(activity1, activity2);

        when(activityRepository.findAll()).thenReturn(activities);

        double expectedAverage = (activity1.getMet() + activity2.getMet()) / 2;

        // Act
        ActivityAverageMetResponseDto result = activityService.getAverageMet();

        // Assert
        verify(activityRepository, times(1)).findAll();
        assertEquals(expectedAverage, result.getMet());
    }

    @Test
    void getAverageMet_shouldReturnZero_whenActivitiesNotFound() {
        // Arrange
        when(activityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ActivityAverageMetResponseDto result = activityService.getAverageMet();

        // Assert
        assertEquals(0.0, result.getMet());
        verify(activityRepository, times(1)).findAll();
    }
}

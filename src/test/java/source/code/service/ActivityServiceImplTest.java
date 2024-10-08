package source.code.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.ActivityCreateDto;
import source.code.dto.response.ActivityResponseDto;
import source.code.helper.CalculationsHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.ActivityMapper;
import source.code.model.Activity;
import source.code.repository.ActivityCategoryRepository;
import source.code.repository.ActivityRepository;
import source.code.repository.UserActivityRepository;
import source.code.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

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

        //Asert
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
}

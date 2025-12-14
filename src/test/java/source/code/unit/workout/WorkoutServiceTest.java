package source.code.unit.workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.workout.WorkoutCreateDto;
import source.code.dto.request.workout.WorkoutUpdateDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.WorkoutMapper;
import source.code.model.workout.Workout;
import source.code.repository.WorkoutRepository;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.helpers.ValidationServiceImpl;
import source.code.service.implementation.workout.WorkoutServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {
    @Mock
    private JsonPatchServiceImpl jsonPatchService;

    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private WorkoutMapper workoutMapper;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    private int workoutId;
    private Workout workout;
    private WorkoutUpdateDto workoutUpdateDto;
    private WorkoutCreateDto workoutCreateDto;
    private WorkoutResponseDto workoutResponseDto;
    private JsonMergePatch patch;

    @BeforeEach
    public void setUp() {
        workoutId = 1;
        workout = new Workout();
        workoutUpdateDto = new WorkoutUpdateDto();
        workoutCreateDto = new WorkoutCreateDto();
        workoutResponseDto = new WorkoutResponseDto();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    public void createWorkout() {
        workout.setId(workoutId);
        when(workoutMapper.toEntity(workoutCreateDto)).thenReturn(workout);
        when(workoutRepository.save(workout)).thenReturn(workout);
        when(workoutRepository.findByIdWithDetails(workoutId)).thenReturn(Optional.of(workout));
        when(workoutMapper.toResponseDto(workout)).thenReturn(workoutResponseDto);

        workoutService.createWorkout(workoutCreateDto);

        verify(workoutMapper).toEntity(workoutCreateDto);
        verify(workoutRepository).save(workout);
        verify(workoutRepository).findByIdWithDetails(workoutId);
        verify(workoutMapper).toResponseDto(workout);
    }

    @Test
    public void updateWorkout() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId)).thenReturn(workout);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutUpdateDto.class)))
                .thenReturn(workoutUpdateDto);
        doNothing().when(validationService).validate(workoutUpdateDto);
        doNothing().when(workoutMapper).updateWorkout(workout, workoutUpdateDto);
        when(workoutRepository.save(workout)).thenReturn(workout);

        workoutService.updateWorkout(workoutId, patch);

        verify(repositoryHelper).find(workoutRepository, Workout.class, workoutId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutUpdateDto.class));
        verify(validationService).validate(workoutUpdateDto);
        verify(workoutMapper).updateWorkout(workout, workoutUpdateDto);
        verify(workoutRepository).save(workout);
    }

    @Test
    public void updateWorkoutNotFound() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutService.updateWorkout(workoutId, patch));

        verify(repositoryHelper).find(workoutRepository, Workout.class, workoutId);
        verify(jsonPatchService, never()).createFromPatch(eq(patch), eq(WorkoutUpdateDto.class));
        verify(validationService, never()).validate(workoutUpdateDto);
        verify(workoutMapper, never()).updateWorkout(workout, workoutUpdateDto);
        verify(workoutRepository, never()).save(workout);
    }

    @Test
    public void updateWorkoutValidationFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId)).thenReturn(workout);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutUpdateDto.class)))
                .thenReturn(workoutUpdateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(workoutUpdateDto);

        assertThrows(IllegalArgumentException.class, () -> workoutService.updateWorkout(workoutId, patch));

        verify(repositoryHelper).find(workoutRepository, Workout.class, workoutId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutUpdateDto.class));
        verify(validationService).validate(workoutUpdateDto);
        verify(workoutMapper, never()).updateWorkout(workout, workoutUpdateDto);
        verify(workoutRepository, never()).save(workout);
    }

    @Test
    public void updateWorkoutPatchApplicationFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId)).thenReturn(workout);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutUpdateDto.class)))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> workoutService.updateWorkout(workoutId, patch));

        verify(repositoryHelper).find(workoutRepository, Workout.class, workoutId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutUpdateDto.class));
        verify(validationService, never()).validate(workoutUpdateDto);
        verify(workoutMapper, never()).updateWorkout(workout, workoutUpdateDto);
        verify(workoutRepository, never()).save(workout);
    }

    @Test
    public void deleteWorkout() {
        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId)).thenReturn(workout);
        doNothing().when(workoutRepository).delete(workout);

        workoutService.deleteWorkout(workoutId);

        verify(repositoryHelper).find(workoutRepository, Workout.class, workoutId);
        verify(workoutRepository).delete(workout);
    }

    @Test
    public void deleteWorkoutNotFound() {
        when(repositoryHelper.find(workoutRepository, Workout.class, workoutId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutService.deleteWorkout(workoutId));

        verify(repositoryHelper).find(workoutRepository, Workout.class, workoutId);
        verify(workoutRepository, never()).delete(workout);
    }

    @Test
    public void getWorkout() {
        when(workoutRepository.findByIdWithDetails(workoutId)).thenReturn(Optional.of(workout));
        when(workoutMapper.toResponseDto(workout)).thenReturn(workoutResponseDto);

        WorkoutResponseDto result = workoutService.getWorkout(workoutId);

        verify(workoutRepository).findByIdWithDetails(workoutId);
        verify(workoutMapper).toResponseDto(workout);
    }

    @Test
    public void getWorkoutNotFound() {
        when(workoutRepository.findByIdWithDetails(workoutId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> workoutService.getWorkout(workoutId));

        verify(workoutRepository).findByIdWithDetails(workoutId);
        verify(workoutMapper, never()).toResponseDto(workout);
    }

    @Test
    public void getAllWorkoutsForPlan() {
        int planId = 1;
        when(workoutRepository.findAllByPlanId(planId)).thenReturn(List.of(workout));
        when(workoutMapper.toResponseDto(workout)).thenReturn(workoutResponseDto);

        List<WorkoutResponseDto> result = workoutService.getAllWorkoutsForPlan(planId);

        verify(workoutRepository).findAllByPlanId(planId);
        verify(workoutMapper).toResponseDto(workout);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllWorkoutsForPlanNoWorkouts() {
        int planId = 1;
        when(workoutRepository.findAllByPlanId(planId)).thenReturn(List.of());

        List<WorkoutResponseDto> result = workoutService.getAllWorkoutsForPlan(planId);

        verify(workoutRepository).findAllByPlanId(planId);
        assertEquals(0, result.size());
    }
}

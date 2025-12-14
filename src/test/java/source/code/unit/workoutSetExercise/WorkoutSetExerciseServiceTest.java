package source.code.unit.workoutSetExercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseCreateDto;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseUpdateDto;
import source.code.dto.response.workoutSetExercise.WorkoutSetExerciseResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.WorkoutSetExerciseMapper;
import source.code.model.workout.WorkoutSetExercise;
import source.code.repository.WorkoutSetExerciseRepository;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.workoutSetExercise.WorkoutSetExerciseServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetExerciseServiceTest {
    @Mock
    private JsonPatchServiceImpl jsonPatchService;

    @Mock
    private ValidationService validationService;

    @Mock
    private WorkoutSetExerciseMapper workoutSetExerciseMapper;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private WorkoutSetExerciseRepository workoutSetExerciseRepository;

    @InjectMocks
    private WorkoutSetExerciseServiceImpl workoutSetExerciseService;

    private int workoutSetExerciseId;
    private WorkoutSetExercise workoutSetExercise;
    private WorkoutSetExerciseCreateDto createDto;
    private WorkoutSetExerciseUpdateDto updateDto;
    private WorkoutSetExerciseResponseDto responseDto;
    private JsonMergePatch patch;

    @BeforeEach
    public void setUp() {
        workoutSetExerciseId = 1;
        workoutSetExercise = new WorkoutSetExercise();
        createDto = new WorkoutSetExerciseCreateDto();
        updateDto = new WorkoutSetExerciseUpdateDto();
        responseDto = new WorkoutSetExerciseResponseDto();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    public void createWorkoutSetExercise_ShouldCreateNewWorkoutSetExercise() {
        when(workoutSetExerciseMapper.toEntity(createDto)).thenReturn(workoutSetExercise);
        when(workoutSetExerciseRepository.save(workoutSetExercise)).thenReturn(workoutSetExercise);
        when(workoutSetExerciseMapper.toResponseDto(workoutSetExercise)).thenReturn(responseDto);

        workoutSetExerciseService.createWorkoutSetExercise(createDto);

        verify(workoutSetExerciseMapper).toEntity(createDto);
        verify(workoutSetExerciseRepository).save(workoutSetExercise);
        verify(workoutSetExerciseMapper).toResponseDto(workoutSetExercise);
    }

    @Test
    public void updateWorkoutSetExercise_ShouldUpdateExistingWorkoutSetExercise() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId)).thenReturn(workoutSetExercise);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetExerciseUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(workoutSetExerciseMapper).updateWorkoutSetExercise(workoutSetExercise, updateDto);
        when(workoutSetExerciseRepository.save(workoutSetExercise)).thenReturn(workoutSetExercise);

        workoutSetExerciseService.updateWorkoutSetExercise(workoutSetExerciseId, patch);

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetExerciseUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(workoutSetExerciseMapper).updateWorkoutSetExercise(workoutSetExercise, updateDto);
        verify(workoutSetExerciseRepository).save(workoutSetExercise);
    }

    @Test
    public void updateWorkoutSetExercise_ShouldThrowExceptionWhenWorkoutSetExerciseNotFound() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetExerciseService.updateWorkoutSetExercise(workoutSetExerciseId, patch));

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(workoutSetExerciseMapper, never()).toResponseDto(workoutSetExercise);
        verify(jsonPatchService, never()).createFromPatch(any(), any());
        verify(validationService, never()).validate(any());
        verify(workoutSetExerciseMapper, never()).updateWorkoutSetExercise(any(), any());
        verify(workoutSetExerciseRepository, never()).save(any());
    }

    @Test
    public void updateWorkoutSetExercise_ShouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId)).thenReturn(workoutSetExercise);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetExerciseUpdateDto.class)))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> workoutSetExerciseService.updateWorkoutSetExercise(workoutSetExerciseId, patch));

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetExerciseUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(workoutSetExerciseMapper, never()).updateWorkoutSetExercise(any(), any());
        verify(workoutSetExerciseRepository, never()).save(any());
    }

    @Test
    public void updateWorkoutSetExercise_ShouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId)).thenReturn(workoutSetExercise);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetExerciseUpdateDto.class)))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> workoutSetExerciseService.updateWorkoutSetExercise(workoutSetExerciseId, patch));

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetExerciseUpdateDto.class));
        verify(validationService, never()).validate(any());
        verify(workoutSetExerciseMapper, never()).updateWorkoutSetExercise(any(), any());
        verify(workoutSetExerciseRepository, never()).save(any());
    }

    @Test
    public void deleteWorkoutSetExercise_ShouldDeleteExistingWorkoutSetExercise() {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId)).thenReturn(workoutSetExercise);
        doNothing().when(workoutSetExerciseRepository).delete(workoutSetExercise);

        workoutSetExerciseService.deleteWorkoutSetExercise(workoutSetExerciseId);

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(workoutSetExerciseRepository).delete(workoutSetExercise);
    }

    @Test
    public void deleteWorkoutSetExercise_ShouldThrowExceptionWhenWorkoutSetExerciseNotFound() {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetExerciseService.deleteWorkoutSetExercise(workoutSetExerciseId));

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(workoutSetExerciseRepository, never()).delete(any());
    }

    @Test
    public void getWorkoutSetExercise_ShouldReturnWorkoutSetExerciseById() {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId)).thenReturn(workoutSetExercise);
        when(workoutSetExerciseMapper.toResponseDto(workoutSetExercise)).thenReturn(responseDto);

        WorkoutSetExerciseResponseDto result = workoutSetExerciseService.getWorkoutSetExercise(workoutSetExerciseId);

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(workoutSetExerciseMapper).toResponseDto(workoutSetExercise);
    }

    @Test
    public void getWorkoutSetExercise_ShouldThrowExceptionWhenWorkoutSetExerciseNotFound() {
        when(repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetExerciseService.getWorkoutSetExercise(workoutSetExerciseId));

        verify(repositoryHelper).find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
        verify(workoutSetExerciseMapper, never()).toResponseDto(any());
    }

    @Test
    public void getAllWorkoutSetExercisesForWorkoutSet_ShouldReturnAllWorkoutSetExercisesForWorkoutSet() {
        int workoutSetId = 1;
        when(workoutSetExerciseRepository.findAllByWorkoutSetId(workoutSetId)).thenReturn(List.of(workoutSetExercise));
        when(workoutSetExerciseMapper.toResponseDto(workoutSetExercise)).thenReturn(responseDto);

        List<WorkoutSetExerciseResponseDto> result = workoutSetExerciseService.getAllWorkoutSetExercisesForWorkoutSet(workoutSetId);

        verify(workoutSetExerciseRepository).findAllByWorkoutSetId(workoutSetId);
        verify(workoutSetExerciseMapper).toResponseDto(workoutSetExercise);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllWorkoutSetExercisesForWorkoutSet_ShouldReturnEmptyListWhenNoWorkoutSetExercisesFound() {
        int workoutSetId = 1;
        when(workoutSetExerciseRepository.findAllByWorkoutSetId(workoutSetId)).thenReturn(List.of());

        List<WorkoutSetExerciseResponseDto> result = workoutSetExerciseService.getAllWorkoutSetExercisesForWorkoutSet(workoutSetId);

        verify(workoutSetExerciseRepository).findAllByWorkoutSetId(workoutSetId);
        assertEquals(0, result.size());
    }
}

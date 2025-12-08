package source.code.unit.workoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.workoutSet.WorkoutSetMapper;
import source.code.model.workout.WorkoutSet;
import source.code.repository.WorkoutSetRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.workoutSet.WorkoutSetServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetServiceTest {
    @Mock
    private JsonPatchService jsonPatchService;

    @Mock
    private ValidationService validationService;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @Mock
    private WorkoutSetMapper workoutSetMapper;

    @InjectMocks
    private WorkoutSetServiceImpl workoutSetService;

    private int workoutSetId = 1;
    private WorkoutSet workoutSet;
    private WorkoutSetResponseDto responseDto;
    private WorkoutSetCreateDto createDto;
    private WorkoutSetUpdateDto updateDto;
    private JsonMergePatch patch;

    @BeforeEach
    public void setUp() {
        workoutSetId = 1;
        workoutSet = new WorkoutSet();
        responseDto = new WorkoutSetResponseDto();
        createDto = new WorkoutSetCreateDto();
        updateDto = new WorkoutSetUpdateDto();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    public void createWorkoutSet_ShouldCreateNewWorkoutSet() {
        workoutSet.setId(workoutSetId);
        when(workoutSetMapper.toEntity(createDto)).thenReturn(workoutSet);
        when(workoutSetRepository.save(workoutSet)).thenReturn(workoutSet);
        when(workoutSetRepository.findByIdWithDetails(workoutSetId)).thenReturn(Optional.of(workoutSet));
        when(workoutSetMapper.toResponseDto(workoutSet)).thenReturn(responseDto);

        WorkoutSetResponseDto result = workoutSetService.createWorkoutSet(createDto);

        verify(workoutSetMapper).toEntity(createDto);
        verify(workoutSetRepository).save(workoutSet);
        verify(workoutSetRepository).findByIdWithDetails(workoutSetId);
        verify(workoutSetMapper).toResponseDto(workoutSet);
        assertEquals(responseDto, result);
    }

    @Test
    public void updateWorkoutSet_ShouldUpdateExistingWorkoutSet() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(workoutSetMapper).updateWorkoutSet(workoutSet, updateDto);
        when(workoutSetRepository.save(workoutSet)).thenReturn(workoutSet);

        workoutSetService.updateWorkoutSet(workoutSetId, patch);

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(workoutSetMapper).updateWorkoutSet(workoutSet, updateDto);
        verify(workoutSetRepository).save(workoutSet);
    }

    @Test
    public void updateWorkoutSet_ShouldThrowExceptionWhenNotFound() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetService.updateWorkoutSet(workoutSetId, patch));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(jsonPatchService, never()).createFromPatch(any(), any());
        verify(validationService, never()).validate(any());
        verify(workoutSetMapper, never()).updateWorkoutSet(any(), any());
        verify(workoutSetRepository, never()).save(any());
    }

    @Test
    public void updateWorkoutSet_ShouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetUpdateDto.class)))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> workoutSetService.updateWorkoutSet(workoutSetId, patch));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(workoutSetMapper, never()).updateWorkoutSet(any(), any());
        verify(workoutSetRepository, never()).save(any());
    }

    @Test
    public void updateWorkoutSet_ShouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetUpdateDto.class)))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> workoutSetService.updateWorkoutSet(workoutSetId, patch));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetUpdateDto.class));
        verify(validationService, never()).validate(any());
        verify(workoutSetMapper, never()).updateWorkoutSet(any(), any());
        verify(workoutSetRepository, never()).save(any());
    }

    @Test
    public void deleteWorkoutSet_ShouldDeleteExistingWorkoutSet() {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
        doNothing().when(workoutSetRepository).delete(workoutSet);

        workoutSetService.deleteWorkoutSet(workoutSetId);

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetRepository).delete(workoutSet);
    }

    @Test
    public void deleteWorkoutSet_ShouldThrowExceptionWhenNotFound() {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetService.deleteWorkoutSet(workoutSetId));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetRepository, never()).delete(any());
    }

    @Test
    public void getWorkoutSet_ShouldReturnWorkoutSetById() {
        when(workoutSetRepository.findByIdWithDetails(workoutSetId)).thenReturn(Optional.of(workoutSet));
        when(workoutSetMapper.toResponseDto(workoutSet)).thenReturn(responseDto);

        WorkoutSetResponseDto result = workoutSetService.getWorkoutSet(workoutSetId);

        verify(workoutSetRepository).findByIdWithDetails(workoutSetId);
        verify(workoutSetMapper).toResponseDto(workoutSet);
        assertEquals(responseDto, result);
    }

    @Test
    public void getWorkoutSet_ShouldThrowExceptionWhenNotFound() {
        when(workoutSetRepository.findByIdWithDetails(workoutSetId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> workoutSetService.getWorkoutSet(workoutSetId));

        verify(workoutSetRepository).findByIdWithDetails(workoutSetId);
        verify(workoutSetMapper, never()).toResponseDto(any());
    }

    @Test
    public void getAllWorkoutSetsForWorkout_ShouldReturnAllWorkoutSetsForWorkout() {
        int workoutId = 1;
        when(workoutSetRepository.findAllByWorkoutId(workoutId)).thenReturn(List.of(workoutSet));
        when(workoutSetMapper.toResponseDto(workoutSet)).thenReturn(responseDto);

        List<WorkoutSetResponseDto> result = workoutSetService.getAllWorkoutSetsForWorkout(workoutId);

        verify(workoutSetRepository).findAllByWorkoutId(workoutId);
        verify(workoutSetMapper).toResponseDto(workoutSet);
        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
    }

    @Test
    public void getAllWorkoutSetsForWorkout_ShouldReturnEmptyListWhenNoWorkoutSetsFound() {
        int workoutId = 1;
        when(workoutSetRepository.findAllByWorkoutId(workoutId)).thenReturn(List.of());

        List<WorkoutSetResponseDto> result = workoutSetService.getAllWorkoutSetsForWorkout(workoutId);

        verify(workoutSetRepository).findAllByWorkoutId(workoutId);
        verify(workoutSetMapper, never()).toResponseDto(any());
        assertEquals(0, result.size());
    }
}

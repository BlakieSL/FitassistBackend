package source.code.unit.workoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.helpers.JsonPatchServiceImpl;
import source.code.service.implementation.workoutSet.WorkoutSetServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetServiceTest {
    @Mock
    private JsonPatchServiceImpl jsonPatchService;

    @Mock
    private ValidationService validationService;

    @Mock
    private WorkoutSetMapper workoutSetMapper;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @InjectMocks
    private WorkoutSetServiceImpl workoutSetService;

    private int workoutSetId;
    private WorkoutSet workoutSet;
    private WorkoutSetCreateDto createDto;
    private WorkoutSetUpdateDto updateDto;
    private WorkoutSetResponseDto responseDto;
    private JsonMergePatch patch;

    @BeforeEach
    public void setUp() {
        workoutSetId = 1;
        workoutSet = new WorkoutSet();
        createDto = new WorkoutSetCreateDto();
        updateDto = new WorkoutSetUpdateDto();
        responseDto = new WorkoutSetResponseDto();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    @DisplayName("createWorkoutSet - Should create a new WorkoutSet")
    public void createWorkoutSet_ShouldCreateNewWorkoutSet() {
        when(workoutSetMapper.toEntity(createDto)).thenReturn(workoutSet);
        when(workoutSetRepository.save(workoutSet)).thenReturn(workoutSet);
        when(workoutSetMapper.toResponseDto(workoutSet)).thenReturn(responseDto);

        workoutSetService.createWorkoutSet(createDto);

        verify(workoutSetMapper).toEntity(createDto);
        verify(workoutSetRepository).save(workoutSet);
        verify(workoutSetMapper).toResponseDto(workoutSet);
    }

    @Test
    @DisplayName("updateWorkoutSet - Should update an existing WorkoutSet")
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
    @DisplayName("updateWorkoutSet - Should throw exception when workoutSet not found")
    public void updateWorkoutSet_ShouldThrowExceptionWhenWorkoutSetNotFound() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetService.updateWorkoutSet(workoutSetId, patch));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetMapper, never()).toResponseDto(workoutSet);
        verify(jsonPatchService, never()).createFromPatch(any(), any());
        verify(validationService, never()).validate(any());
        verify(workoutSetMapper, never()).updateWorkoutSet(any(), any());
        verify(workoutSetRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateWorkoutSet - Should throw exception when validation fails")
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
    @DisplayName("updateWorkoutSet - Should throw exception when patch fails")
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
    @DisplayName("deleteWorkoutSet - Should delete an existing WorkoutSet")
    public void deleteWorkoutSet_ShouldDeleteExistingWorkoutSet() {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
        doNothing().when(workoutSetRepository).delete(workoutSet);

        workoutSetService.deleteWorkoutSet(workoutSetId);

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetRepository).delete(workoutSet);
    }

    @Test
    @DisplayName("deleteWorkoutSet - Should throw exception when workoutSet not found")
    public void deleteWorkoutSet_ShouldThrowExceptionWhenWorkoutSetNotFound() {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetService.deleteWorkoutSet(workoutSetId));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getWorkoutSet - Should return a WorkoutSet by ID")
    public void getWorkoutSet_ShouldReturnWorkoutSetById() {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId)).thenReturn(workoutSet);
        when(workoutSetMapper.toResponseDto(workoutSet)).thenReturn(responseDto);

        WorkoutSetResponseDto result = workoutSetService.getWorkoutSet(workoutSetId);

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetMapper).toResponseDto(workoutSet);
    }

    @Test
    @DisplayName("getWorkoutSet - Should throw exception when workoutSet not found")
    public void getWorkoutSet_ShouldThrowExceptionWhenWorkoutSetNotFound() {
        when(repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutSetId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetService.getWorkoutSet(workoutSetId));

        verify(repositoryHelper).find(workoutSetRepository, WorkoutSet.class, workoutSetId);
        verify(workoutSetMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("getAllWorkoutSetsForWorkoutSetGroup - Should return all WorkoutSets for a given WorkoutSetGroup")
    public void getAllWorkoutSetsForWorkout_ShouldReturnAllWorkoutSetsForWorkout() {
        int workoutSetGroupId = 1;
        when(workoutSetRepository.findAllByWorkoutSetGroupId(workoutSetGroupId)).thenReturn(List.of(workoutSet));
        when(workoutSetMapper.toResponseDto(workoutSet)).thenReturn(responseDto);

        List<WorkoutSetResponseDto> result = workoutSetService.getAllWorkoutSetsForWorkoutSetGroup(workoutSetGroupId);

        verify(workoutSetRepository).findAllByWorkoutSetGroupId(workoutSetGroupId);
        verify(workoutSetMapper).toResponseDto(workoutSet);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getAllWorkoutSetsForWorkoutSetGroup - Should return empty list when no WorkoutSets found")
    public void getAllWorkoutSetsForWorkout_ShouldReturnEmptyListWhenNoWorkoutSetsFound() {
        int workoutSetGroupId = 1;
        when(workoutSetRepository.findAllByWorkoutSetGroupId(workoutSetGroupId)).thenReturn(List.of());

        List<WorkoutSetResponseDto> result = workoutSetService.getAllWorkoutSetsForWorkoutSetGroup(workoutSetGroupId);

        verify(workoutSetRepository).findAllByWorkoutSetGroupId(workoutSetGroupId);
        assertEquals(0, result.size());
    }
}
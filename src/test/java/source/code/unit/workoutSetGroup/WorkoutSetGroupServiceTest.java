package source.code.unit.workoutSetGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupCreateDto;
import source.code.dto.request.workoutSetGroup.WorkoutSetGroupUpdateDto;
import source.code.dto.response.workoutSetGroup.WorkoutSetGroupResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.workoutSetGroup.WorkoutSetGroupMapper;
import source.code.model.workout.WorkoutSetGroup;
import source.code.repository.WorkoutSetGroupRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.workoutSetGroup.WorkoutSetGroupServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetGroupServiceTest {
    @Mock
    private JsonPatchService jsonPatchService;

    @Mock
    private ValidationService validationService;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private WorkoutSetGroupRepository workoutSetGroupRepository;

    @Mock
    private WorkoutSetGroupMapper workoutSetGroupMapper;

    @InjectMocks
    private WorkoutSetGroupServiceImpl workoutSetGroupService;

    private int workoutSetGroupId = 1;
    private WorkoutSetGroup workoutSetGroup;
    private WorkoutSetGroupResponseDto responseDto;
    private WorkoutSetGroupCreateDto createDto;
    private WorkoutSetGroupUpdateDto updateDto;
    private JsonMergePatch patch;

    @BeforeEach
    public void setUp() {
        workoutSetGroupId = 1;
        workoutSetGroup = new WorkoutSetGroup();
        responseDto = new WorkoutSetGroupResponseDto();
        createDto = new WorkoutSetGroupCreateDto();
        updateDto = new WorkoutSetGroupUpdateDto();
        patch = mock(JsonMergePatch.class);
    }

    @Test
    public void createWorkoutSetGroup_ShouldCreateNewWorkoutSetGroup() {
        workoutSetGroup.setId(workoutSetGroupId);
        when(workoutSetGroupMapper.toEntity(createDto)).thenReturn(workoutSetGroup);
        when(workoutSetGroupRepository.save(workoutSetGroup)).thenReturn(workoutSetGroup);
        when(workoutSetGroupRepository.findByIdWithDetails(workoutSetGroupId)).thenReturn(Optional.of(workoutSetGroup));
        when(workoutSetGroupMapper.toResponseDto(workoutSetGroup)).thenReturn(responseDto);

        WorkoutSetGroupResponseDto result = workoutSetGroupService.createWorkoutSetGroup(createDto);

        verify(workoutSetGroupMapper).toEntity(createDto);
        verify(workoutSetGroupRepository).save(workoutSetGroup);
        verify(workoutSetGroupRepository).findByIdWithDetails(workoutSetGroupId);
        verify(workoutSetGroupMapper).toResponseDto(workoutSetGroup);
        assertEquals(responseDto, result);
    }

    @Test
    public void updateWorkoutSetGroup_ShouldUpdateExistingWorkoutSetGroup() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId)).thenReturn(workoutSetGroup);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetGroupUpdateDto.class)))
                .thenReturn(updateDto);
        doNothing().when(validationService).validate(updateDto);
        doNothing().when(workoutSetGroupMapper).updateWorkoutSetGroup(workoutSetGroup, updateDto);
        when(workoutSetGroupRepository.save(workoutSetGroup)).thenReturn(workoutSetGroup);

        workoutSetGroupService.updateWorkoutSetGroup(workoutSetGroupId, patch);

        verify(repositoryHelper).find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetGroupUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(workoutSetGroupMapper).updateWorkoutSetGroup(workoutSetGroup, updateDto);
        verify(workoutSetGroupRepository).save(workoutSetGroup);
    }

    @Test
    public void updateWorkoutSetGroup_ShouldThrowExceptionWhenNotFound() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetGroupService.updateWorkoutSetGroup(workoutSetGroupId, patch));

        verify(repositoryHelper).find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
        verify(jsonPatchService, never()).createFromPatch(any(), any());
        verify(validationService, never()).validate(any());
        verify(workoutSetGroupMapper, never()).updateWorkoutSetGroup(any(), any());
        verify(workoutSetGroupRepository, never()).save(any());
    }

    @Test
    public void updateWorkoutSetGroup_ShouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId)).thenReturn(workoutSetGroup);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetGroupUpdateDto.class)))
                .thenReturn(updateDto);
        doThrow(IllegalArgumentException.class).when(validationService).validate(updateDto);

        assertThrows(IllegalArgumentException.class, () -> workoutSetGroupService.updateWorkoutSetGroup(workoutSetGroupId, patch));

        verify(repositoryHelper).find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetGroupUpdateDto.class));
        verify(validationService).validate(updateDto);
        verify(workoutSetGroupMapper, never()).updateWorkoutSetGroup(any(), any());
        verify(workoutSetGroupRepository, never()).save(any());
    }

    @Test
    public void updateWorkoutSetGroup_ShouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId)).thenReturn(workoutSetGroup);
        when(jsonPatchService.createFromPatch(eq(patch), eq(WorkoutSetGroupUpdateDto.class)))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> workoutSetGroupService.updateWorkoutSetGroup(workoutSetGroupId, patch));

        verify(repositoryHelper).find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
        verify(jsonPatchService).createFromPatch(eq(patch), eq(WorkoutSetGroupUpdateDto.class));
        verify(validationService, never()).validate(any());
        verify(workoutSetGroupMapper, never()).updateWorkoutSetGroup(any(), any());
        verify(workoutSetGroupRepository, never()).save(any());
    }

    @Test
    public void deleteWorkoutSetGroup_ShouldDeleteExistingWorkoutSetGroup() {
        when(repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId)).thenReturn(workoutSetGroup);
        doNothing().when(workoutSetGroupRepository).delete(workoutSetGroup);

        workoutSetGroupService.deleteWorkoutSetGroup(workoutSetGroupId);

        verify(repositoryHelper).find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
        verify(workoutSetGroupRepository).delete(workoutSetGroup);
    }

    @Test
    public void deleteWorkoutSetGroup_ShouldThrowExceptionWhenNotFound() {
        when(repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId))
                .thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> workoutSetGroupService.deleteWorkoutSetGroup(workoutSetGroupId));

        verify(repositoryHelper).find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
        verify(workoutSetGroupRepository, never()).delete(any());
    }

    @Test
    public void getWorkoutSetGroup_ShouldReturnWorkoutSetGroupById() {
        when(workoutSetGroupRepository.findByIdWithDetails(workoutSetGroupId)).thenReturn(Optional.of(workoutSetGroup));
        when(workoutSetGroupMapper.toResponseDto(workoutSetGroup)).thenReturn(responseDto);

        WorkoutSetGroupResponseDto result = workoutSetGroupService.getWorkoutSetGroup(workoutSetGroupId);

        verify(workoutSetGroupRepository).findByIdWithDetails(workoutSetGroupId);
        verify(workoutSetGroupMapper).toResponseDto(workoutSetGroup);
        assertEquals(responseDto, result);
    }

    @Test
    public void getWorkoutSetGroup_ShouldThrowExceptionWhenNotFound() {
        when(workoutSetGroupRepository.findByIdWithDetails(workoutSetGroupId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> workoutSetGroupService.getWorkoutSetGroup(workoutSetGroupId));

        verify(workoutSetGroupRepository).findByIdWithDetails(workoutSetGroupId);
        verify(workoutSetGroupMapper, never()).toResponseDto(any());
    }

    @Test
    public void getAllWorkoutSetGroupsForWorkout_ShouldReturnAllWorkoutSetGroupsForWorkout() {
        int workoutId = 1;
        when(workoutSetGroupRepository.findAllByWorkoutId(workoutId)).thenReturn(List.of(workoutSetGroup));
        when(workoutSetGroupMapper.toResponseDto(workoutSetGroup)).thenReturn(responseDto);

        List<WorkoutSetGroupResponseDto> result = workoutSetGroupService.getAllWorkoutSetGroupsForWorkout(workoutId);

        verify(workoutSetGroupRepository).findAllByWorkoutId(workoutId);
        verify(workoutSetGroupMapper).toResponseDto(workoutSetGroup);
        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
    }

    @Test
    public void getAllWorkoutSetGroupsForWorkout_ShouldReturnEmptyListWhenNoWorkoutSetGroupsFound() {
        int workoutId = 1;
        when(workoutSetGroupRepository.findAllByWorkoutId(workoutId)).thenReturn(List.of());

        List<WorkoutSetGroupResponseDto> result = workoutSetGroupService.getAllWorkoutSetGroupsForWorkout(workoutId);

        verify(workoutSetGroupRepository).findAllByWorkoutId(workoutId);
        verify(workoutSetGroupMapper, never()).toResponseDto(any());
        assertEquals(0, result.size());
    }
}

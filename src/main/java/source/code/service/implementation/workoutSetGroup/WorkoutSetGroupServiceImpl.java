package source.code.service.implementation.workoutSetGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
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
import source.code.service.declaration.workoutSetGroup.WorkoutSetGroupService;

import java.util.List;

@Service
public class WorkoutSetGroupServiceImpl implements WorkoutSetGroupService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final RepositoryHelper repositoryHelper;
    private final WorkoutSetGroupRepository workoutSetGroupRepository;
    private final WorkoutSetGroupMapper workoutSetGroupMapper;

    public WorkoutSetGroupServiceImpl(JsonPatchService jsonPatchService,
                                      ValidationService validationService,
                                      RepositoryHelper repositoryHelper,
                                      WorkoutSetGroupRepository workoutSetGroupRepository,
                                      WorkoutSetGroupMapper workoutSetGroupMapper) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.repositoryHelper = repositoryHelper;
        this.workoutSetGroupRepository = workoutSetGroupRepository;
        this.workoutSetGroupMapper = workoutSetGroupMapper;
    }

    @Transactional
    @Override
    public WorkoutSetGroupResponseDto createWorkoutSetGroup(WorkoutSetGroupCreateDto createDto) {
        WorkoutSetGroup saved = workoutSetGroupRepository.save(workoutSetGroupMapper.toEntity(createDto));

        workoutSetGroupRepository.flush();

        return findAndMap(saved.getId());
    }

    @Transactional
    @Override
    public void updateWorkoutSetGroup(int workoutSetGroupId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        WorkoutSetGroup workoutSetGroup = find(workoutSetGroupId);
        WorkoutSetGroupUpdateDto patched = applyPatchToWorkoutSetGroup(patch);

        validationService.validate(patched);
        workoutSetGroupMapper.updateWorkoutSetGroup(workoutSetGroup, patched);
        workoutSetGroupRepository.save(workoutSetGroup);
    }

    @Transactional
    @Override
    public void deleteWorkoutSetGroup(int workoutSetGroupId) {
        WorkoutSetGroup workoutSetGroup = find(workoutSetGroupId);
        workoutSetGroupRepository.delete(workoutSetGroup);
    }

    @Override
    public WorkoutSetGroupResponseDto getWorkoutSetGroup(int workoutSetGroupId) {
        return findAndMap(workoutSetGroupId);
    }

    @Override
    public List<WorkoutSetGroupResponseDto> getAllWorkoutSetGroupsForWorkout(int workoutId) {
        return workoutSetGroupRepository.findAllByWorkoutId(workoutId).stream()
                .map(workoutSetGroupMapper::toResponseDto)
                .toList();
    }

    private WorkoutSetGroup find(int workoutSetGroupId) {
        return repositoryHelper.find(workoutSetGroupRepository, WorkoutSetGroup.class, workoutSetGroupId);
    }

    private WorkoutSetGroupResponseDto findAndMap(int workoutSetGroupId) {
        WorkoutSetGroup workoutSetGroup = workoutSetGroupRepository.findByIdWithDetails(workoutSetGroupId)
                .orElseThrow(() -> new RecordNotFoundException(WorkoutSetGroup.class, workoutSetGroupId));
        return workoutSetGroupMapper.toResponseDto(workoutSetGroup);
    }

    private WorkoutSetGroupUpdateDto applyPatchToWorkoutSetGroup(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return jsonPatchService.createFromPatch(patch, WorkoutSetGroupUpdateDto.class);
    }
}

package source.code.service.implementation.workoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetUpdateDto;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;
import source.code.mapper.workoutSet.WorkoutSetMapper;
import source.code.model.workout.WorkoutSet;
import source.code.repository.WorkoutSetRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.workoutSet.WorkoutSetService;

import java.util.List;

@Service
public class WorkoutSetServiceImpl implements WorkoutSetService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final WorkoutSetMapper workoutSetMapper;
    private final RepositoryHelper repositoryHelper;
    private final WorkoutSetRepository workoutSetRepository;

    public WorkoutSetServiceImpl(JsonPatchService jsonPatchService,
                                 ValidationService validationService,
                                 WorkoutSetMapper workoutSetMapper,
                                 RepositoryHelper repositoryHelper,
                                 WorkoutSetRepository workoutSetRepository) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.workoutSetMapper = workoutSetMapper;
        this.repositoryHelper = repositoryHelper;
        this.workoutSetRepository = workoutSetRepository;
    }

    @Override
    @Transactional
    public WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto createDto) {
        WorkoutSet workoutSet = workoutSetRepository.save(workoutSetMapper.toEntity(createDto));
        return workoutSetMapper.toResponseDto(workoutSet);
    }

    @Override
    @Transactional
    public void updateWorkoutSet(int workoutSetId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        WorkoutSet workoutSet = find(workoutSetId);
        WorkoutSetUpdateDto patched = applyPatchToWorkoutSet(patch);

        validationService.validate(patched);
        workoutSetMapper.updateWorkoutSet(workoutSet, patched);
        workoutSetRepository.save(workoutSet);
    }

    @Override
    @Transactional
    public void deleteWorkoutSet(int workoutSetId) {
        WorkoutSet workoutSet = find(workoutSetId);
        workoutSetRepository.delete(workoutSet);
    }

    @Override
    public WorkoutSetResponseDto getWorkoutSet(int workoutSetId) {
        WorkoutSet workoutSet = find(workoutSetId);
        return workoutSetMapper.toResponseDto(workoutSet);
    }

    @Override
    public List<WorkoutSetResponseDto> getAllWorkoutSetsForWorkoutSetGroup(int workoutSetGroupId) {
        return workoutSetRepository.findAllByWorkoutSetGroupId(workoutSetGroupId).stream()
                .map(workoutSetMapper::toResponseDto)
                .toList();
    }

    private WorkoutSet find(int workoutId) {
        return repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutId);
    }

    private WorkoutSetUpdateDto applyPatchToWorkoutSet(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        return jsonPatchService.createFromPatch(patch, WorkoutSetUpdateDto.class);
    }
}

package source.code.service.implementation.workoutSetExercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseCreateDto;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseUpdateDto;
import source.code.dto.response.workoutSetExercise.WorkoutSetExerciseResponseDto;
import source.code.mapper.WorkoutSetExerciseMapper;
import source.code.model.workout.WorkoutSetExercise;
import source.code.repository.WorkoutSetExerciseRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.workoutSetExercise.WorkoutSetExerciseService;

import java.util.List;

@Service
public class WorkoutSetExerciseServiceImpl implements WorkoutSetExerciseService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final WorkoutSetExerciseMapper workoutSetExerciseMapper;
    private final RepositoryHelper repositoryHelper;
    private final WorkoutSetExerciseRepository workoutSetExerciseRepository;

    public WorkoutSetExerciseServiceImpl(JsonPatchService jsonPatchService,
                                         ValidationService validationService,
                                         WorkoutSetExerciseMapper workoutSetExerciseMapper,
                                         RepositoryHelper repositoryHelper,
                                         WorkoutSetExerciseRepository workoutSetExerciseRepository) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.workoutSetExerciseMapper = workoutSetExerciseMapper;
        this.repositoryHelper = repositoryHelper;
        this.workoutSetExerciseRepository = workoutSetExerciseRepository;
    }

    @Override
    @Transactional
    public WorkoutSetExerciseResponseDto createWorkoutSetExercise(WorkoutSetExerciseCreateDto createDto) {
        WorkoutSetExercise workoutSetExercise = workoutSetExerciseRepository.save(workoutSetExerciseMapper.toEntity(createDto));
        return workoutSetExerciseMapper.toResponseDto(workoutSetExercise);
    }

    @Override
    @Transactional
    public void updateWorkoutSetExercise(int workoutSetExerciseId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        WorkoutSetExercise workoutSetExercise = find(workoutSetExerciseId);
        WorkoutSetExerciseUpdateDto patched = applyPatchToWorkoutSetExercise(patch);

        validationService.validate(patched);
        workoutSetExerciseMapper.updateWorkoutSetExercise(workoutSetExercise, patched);
        workoutSetExerciseRepository.save(workoutSetExercise);
    }

    @Override
    @Transactional
    public void deleteWorkoutSetExercise(int workoutSetExerciseId) {
        WorkoutSetExercise workoutSetExercise = find(workoutSetExerciseId);
        workoutSetExerciseRepository.delete(workoutSetExercise);
    }

    @Override
    public WorkoutSetExerciseResponseDto getWorkoutSetExercise(int workoutSetExerciseId) {
        WorkoutSetExercise workoutSetExercise = find(workoutSetExerciseId);
        return workoutSetExerciseMapper.toResponseDto(workoutSetExercise);
    }

    @Override
    public List<WorkoutSetExerciseResponseDto> getAllWorkoutSetExercisesForWorkoutSet(int workoutSetId) {
        return workoutSetExerciseRepository.findAllByWorkoutSetId(workoutSetId).stream()
                .map(workoutSetExerciseMapper::toResponseDto)
                .toList();
    }

    private WorkoutSetExercise find(int workoutSetExerciseId) {
        return repositoryHelper.find(workoutSetExerciseRepository, WorkoutSetExercise.class, workoutSetExerciseId);
    }

    private WorkoutSetExerciseUpdateDto applyPatchToWorkoutSetExercise(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return jsonPatchService.createFromPatch(patch, WorkoutSetExerciseUpdateDto.class);
    }
}

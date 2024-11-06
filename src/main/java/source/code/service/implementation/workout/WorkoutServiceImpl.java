package source.code.service.implementation.workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import source.code.dto.Request.workout.WorkoutCreateDto;
import source.code.dto.Request.workout.WorkoutUpdateDto;
import source.code.dto.Response.workout.WorkoutResponseDto;
import source.code.mapper.workout.WorkoutMapper;
import source.code.model.workout.Workout;
import source.code.repository.WorkoutRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.workout.WorkoutService;

import java.util.List;

@Service
public class WorkoutServiceImpl implements WorkoutService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final WorkoutMapper workoutMapper;
    private final RepositoryHelper repositoryHelper;
    private final WorkoutRepository workoutRepository;

    public WorkoutServiceImpl(JsonPatchService jsonPatchService,
                              ValidationService validationService,
                              WorkoutMapper workoutMapper,
                              RepositoryHelper repositoryHelper,
                              WorkoutRepository workoutRepository) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.workoutMapper = workoutMapper;
        this.repositoryHelper = repositoryHelper;
        this.workoutRepository = workoutRepository;
    }

    @Override
    public WorkoutResponseDto createWorkout(WorkoutCreateDto workoutDto) {
        Workout workout = workoutRepository.save(workoutMapper.toEntity(workoutDto));
        return workoutMapper.toResponseDto(workout);
    }

    @Override
    public void updateWorkout(int workoutId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Workout workout = find(workoutId);
        WorkoutUpdateDto patched = applyPatchToWorkout(workout, patch);

        validationService.validate(patched);
        workoutMapper.updateWorkout(workout, patched);
        workoutRepository.save(workout);
    }

    @Override
    public void deleteWorkout(int workoutId) {
        Workout workout = find(workoutId);
        workoutRepository.delete(workout);
    }

    @Override
    public WorkoutResponseDto getWorkout(int id) {
        Workout workout = find(id);
        return workoutMapper.toResponseDto(workout);
    }

    @Override
    public List<WorkoutResponseDto> getAllWorkoutsForPlan(int planId) {
        return workoutRepository.findAllByPlanId(planId).stream()
                .map(workoutMapper::toResponseDto)
                .toList();
    }

    private Workout find(int workoutId) {
        return repositoryHelper.find(workoutRepository, Workout.class, workoutId);
    }

    private WorkoutUpdateDto applyPatchToWorkout(Workout workout, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        WorkoutResponseDto responseDto = workoutMapper.toResponseDto(workout);
        return jsonPatchService.applyPatch(patch, responseDto, WorkoutUpdateDto.class);
    }
}

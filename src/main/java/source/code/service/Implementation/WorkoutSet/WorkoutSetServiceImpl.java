package source.code.service.Implementation.WorkoutSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import source.code.dto.Request.WorkoutSet.WorkoutSetCreateDto;
import source.code.dto.Request.WorkoutSet.WorkoutSetUpdateDto;
import source.code.dto.Response.WorkoutSet.WorkoutSetResponseDto;
import source.code.mapper.WorkoutSet.WorkoutSetMapper;
import source.code.model.Workout.WorkoutSet;
import source.code.repository.WorkoutSetRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.RepositoryHelper;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.WorkoutSet.WorkoutSetService;

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
  public WorkoutSetResponseDto createWorkoutSet(WorkoutSetCreateDto createDto) {
    WorkoutSet workoutSet = workoutSetRepository.save(workoutSetMapper.toEntity(createDto));
    return workoutSetMapper.toResponseDto(workoutSet);
  }

  @Override
  public void updateWorkoutSet(int workoutSetId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    WorkoutSet workoutSet = find(workoutSetId);
    WorkoutSetUpdateDto patched = applyPatchToWorkoutSet(workoutSet, patch);

    validationService.validate(patched);

    workoutSetMapper.updateWorkoutSet(workoutSet, patched);
    workoutSetRepository.save(workoutSet);
  }

  @Override
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
  public List<WorkoutSetResponseDto> getAllWorkoutSetsForWorkout(int workoutId) {
    return workoutSetRepository.getAllByWorkoutId(workoutId).stream()
            .map(workoutSetMapper::toResponseDto)
            .toList();
  }

  private WorkoutSet find(int workoutId) {
    return repositoryHelper.find(workoutSetRepository, WorkoutSet.class, workoutId);
  }

  private WorkoutSetUpdateDto applyPatchToWorkoutSet(WorkoutSet workoutSet, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    WorkoutSetResponseDto responseDto = workoutSetMapper.toResponseDto(workoutSet);
    return jsonPatchService.applyPatch(patch, responseDto, WorkoutSetUpdateDto.class);
  }
}

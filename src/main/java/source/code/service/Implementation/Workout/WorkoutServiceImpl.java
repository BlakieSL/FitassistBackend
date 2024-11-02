package source.code.service.Implementation.Workout;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Workout.WorkoutCreateDto;
import source.code.dto.Response.Workout.WorkoutResponseDto;
import source.code.repository.WorkoutRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.RepositoryHelper;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.Workout.WorkoutService;

import java.util.List;

public class WorkoutServiceImpl implements WorkoutService {
  private final JsonPatchService jsonPatchService;
  private final ValidationService validationService;
  private final RepositoryHelper repositoryHelper;
  private final WorkoutRepository workoutRepository;

  public WorkoutServiceImpl(JsonPatchService jsonPatchService,
                            ValidationService validationService,
                            RepositoryHelper repositoryHelper,
                            WorkoutRepository workoutRepository) {
    this.jsonPatchService = jsonPatchService;
    this.validationService = validationService;
    this.repositoryHelper = repositoryHelper;
    this.workoutRepository = workoutRepository;
  }

  @Override
  public WorkoutResponseDto createWorkout(WorkoutCreateDto workoutDto) {
    return null;
  }

  @Override
  public void updateWorkout(int workoutId, JsonMergePatch patch) {

  }

  @Override
  public void deleteWorkout(int workoutId) {

  }

  @Override
  public WorkoutResponseDto getWorkout(int id) {
    return null;
  }

  @Override
  public List<WorkoutResponseDto> getAllWorkoutsForPlan(int planId) {
    return null;
  }
}

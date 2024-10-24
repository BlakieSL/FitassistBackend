package source.code.service.implementation.Workout;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.Workout.WorkoutCreateDto;
import source.code.dto.response.WorkoutResponseDto;
import source.code.mapper.WorkoutMapper;
import source.code.model.Workout.Workout;
import source.code.model.Workout.WorkoutPlan;
import source.code.model.Workout.WorkoutType;
import source.code.repository.WorkoutPlanRepository;
import source.code.repository.WorkoutRepository;
import source.code.repository.WorkoutTypeRepository;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Workout.WorkoutService;

import java.util.List;

@Service
public class WorkoutServiceImpl implements WorkoutService {
  private final WorkoutMapper workoutMapper;
  private final RepositoryHelper repositoryHelper;
  private final WorkoutRepository workoutRepository;
  private final WorkoutPlanRepository workoutPlanRepository;
  private final WorkoutTypeRepository workoutTypeRepository;

  public WorkoutServiceImpl(WorkoutMapper workoutMapper,
                            RepositoryHelper repositoryHelper,
                            WorkoutRepository workoutRepository,
                            WorkoutPlanRepository workoutPlanRepository,
                            WorkoutTypeRepository workoutTypeRepository) {
    this.workoutMapper = workoutMapper;
    this.repositoryHelper = repositoryHelper;
    this.workoutRepository = workoutRepository;
    this.workoutPlanRepository = workoutPlanRepository;
    this.workoutTypeRepository = workoutTypeRepository;
  }

  @Transactional
  public WorkoutResponseDto createWorkout(WorkoutCreateDto dto) {
    WorkoutType workoutType = repositoryHelper
            .find(workoutTypeRepository, WorkoutType.class, dto.getWorkoutTypeId());
    Workout workout = workoutMapper.toEntity(dto, workoutType);
    Workout savedWorkout = workoutRepository.save(workout);

    return workoutMapper.toResponseDto(savedWorkout);
  }

  public WorkoutResponseDto getWorkout(int workoutId) {
    Workout workout = repositoryHelper.find(workoutRepository, Workout.class, workoutId);
    return workoutMapper.toResponseDto(workout);
  }

  public List<WorkoutResponseDto> getAllWorkouts() {
    return repositoryHelper.findAll(workoutRepository, workoutMapper::toResponseDto);
  }

  public List<WorkoutResponseDto> getWorkoutsByPlan(int planId) {
    return workoutPlanRepository.findByPlanId(planId).stream()
            .map(WorkoutPlan::getWorkout)
            .map(workoutMapper::toResponseDto)
            .toList();
  }
}
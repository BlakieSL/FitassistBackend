package source.code.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.WorkoutDto;
import source.code.mapper.WorkoutMapper;
import source.code.model.Workout.Workout;
import source.code.model.Workout.WorkoutPlan;
import source.code.model.Workout.WorkoutType;
import source.code.repository.WorkoutPlanRepository;
import source.code.repository.WorkoutRepository;
import source.code.repository.WorkoutTypeRepository;
import source.code.service.declaration.WorkoutService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkoutServiceImpl implements WorkoutService {
  private final WorkoutMapper workoutMapper;
  private final WorkoutRepository workoutRepository;
  private final WorkoutPlanRepository workoutPlanRepository;
  private final WorkoutTypeRepository workoutTypeRepository;

  public WorkoutServiceImpl(WorkoutMapper workoutMapper,
                            WorkoutRepository workoutRepository,
                            WorkoutPlanRepository workoutPlanRepository,
                            WorkoutTypeRepository workoutTypeRepository) {
    this.workoutMapper = workoutMapper;
    this.workoutRepository = workoutRepository;
    this.workoutPlanRepository = workoutPlanRepository;
    this.workoutTypeRepository = workoutTypeRepository;
  }

  @Transactional
  public WorkoutDto createWorkout(WorkoutDto dto) {
    WorkoutType workoutType = workoutTypeRepository
            .findById(dto.getWorkoutTypeId())
            .orElseThrow(() -> new NoSuchElementException(
                    "Workout type with id: "
                            + dto.getWorkoutTypeId() + " not found"));
    Workout workout = workoutMapper.toEntity(dto);
    workout.setWorkoutType(workoutType);

    Workout savedWorkout = workoutRepository.save(workout);

    return workoutMapper.toDto(savedWorkout);
  }

  public WorkoutDto getWorkout(int id) {
    Workout workout = workoutRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Workout with id: " + id + " not found"));

    return workoutMapper.toDto(workout);
  }

  public List<WorkoutDto> getAllWorkouts() {
    List<Workout> workouts = workoutRepository.findAll();

    return workouts.stream()
            .map(workoutMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<WorkoutDto> getWorkoutsByPlan(int planId) {
    List<WorkoutPlan> planWorkouts = workoutPlanRepository
            .findByPlanId(planId);
    List<Workout> workouts = planWorkouts
            .stream()
            .map(WorkoutPlan::getWorkout)
            .collect(Collectors.toList());

    return workouts
            .stream()
            .map(workoutMapper::toDto)
            .collect(Collectors.toList());
  }
}
package source.code.service.implementation.Workout;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.model.Plan.Plan;
import source.code.model.Workout.Workout;
import source.code.model.Workout.WorkoutPlan;
import source.code.repository.PlanRepository;
import source.code.repository.WorkoutPlanRepository;
import source.code.repository.WorkoutRepository;
import source.code.service.declaration.Helpers.RepositoryHelper;
import source.code.service.declaration.Workout.WorkoutPlanService;

import java.util.NoSuchElementException;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
  private final RepositoryHelper repositoryHelper;
  private final WorkoutPlanRepository workoutPlanRepository;
  private final WorkoutRepository workoutRepository;
  private final PlanRepository planRepository;

  public WorkoutPlanServiceImpl(
          RepositoryHelper repositoryHelper,
          WorkoutPlanRepository workoutPlanRepository,
          WorkoutRepository workoutRepository,
          PlanRepository planRepository) {
    this.repositoryHelper = repositoryHelper;
    this.workoutPlanRepository = workoutPlanRepository;
    this.workoutRepository = workoutRepository;
    this.planRepository = planRepository;
  }

  @Transactional
  public void addWorkoutToPlan(int workoutId, int planId) {
    if(isAlreadyAdded(workoutId, planId)) {
      throw new NotUniqueRecordException(
              "Plan with id: " + planId + " already has workout with id: " + workoutId);
    }

    Plan plan = repositoryHelper.find(planRepository, Plan.class, planId);
    Workout workout = repositoryHelper.find(workoutRepository, Workout.class, workoutId);
    WorkoutPlan planWorkout = WorkoutPlan.createWithWorkoutPlan(workout, plan);

    workoutPlanRepository.save(planWorkout);
  }

  private boolean isAlreadyAdded(int workoutId, int planId) {
    return workoutPlanRepository.existsByWorkoutIdAndPlanId(workoutId, planId);
  }

  @Transactional
  public void deleteWorkoutFromPlan(int workoutId, int planId) {
    WorkoutPlan workoutPlan = workoutPlanRepository
            .findByWorkoutIdAndPlanId(workoutId, planId)
            .orElseThrow(() -> new RecordNotFoundException(WorkoutPlan.class, workoutId, planId));

    workoutPlanRepository.delete(workoutPlan);
  }
}
package source.code.service.implementation.Workout;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.exception.NotUniqueRecordException;
import source.code.model.Plan.Plan;
import source.code.model.Workout.Workout;
import source.code.model.Workout.WorkoutPlan;
import source.code.repository.PlanRepository;
import source.code.repository.WorkoutPlanRepository;
import source.code.repository.WorkoutRepository;
import source.code.service.declaration.WorkoutPlanService;

import java.util.NoSuchElementException;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
  private final WorkoutPlanRepository workoutPlanRepository;
  private final WorkoutRepository workoutRepository;
  private final PlanRepository planRepository;

  public WorkoutPlanServiceImpl(
          WorkoutPlanRepository workoutPlanRepository,
          WorkoutRepository workoutRepository,
          PlanRepository planRepository) {
    this.workoutPlanRepository = workoutPlanRepository;
    this.workoutRepository = workoutRepository;
    this.planRepository = planRepository;
  }

  @Transactional
  public void addWorkoutToPlan(int workoutId, int planId) {
    if(isAlreadyAdded(workoutId, planId)) {
      throw new NotUniqueRecordException(
              "Plan with id: " + planId
              + " already has workout with id: " + workoutId);
    }

    Plan plan = planRepository
            .findById(planId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Plan with id: " + planId + " not found"));

    Workout workout = workoutRepository
            .findById(workoutId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Workout with id: " + workoutId + " not found"));


    WorkoutPlan planWorkout = WorkoutPlan
            .createWithWorkoutPlan(workout, plan);
    workoutPlanRepository.save(planWorkout);
  }

  private boolean isAlreadyAdded(int workoutId, int planId) {
    return workoutPlanRepository.existsByWorkoutIdAndPlanId(workoutId, planId);
  }

  @Transactional
  public void deleteWorkoutFromPlan(int workoutId, int planId) {
    WorkoutPlan workoutPlan = workoutPlanRepository
            .findByWorkoutIdAndPlanId(workoutId, planId)
            .orElseThrow(() -> new NoSuchElementException(
                    "PlanWorkout with plan id: " + planId
                            + " and workout id: " + workoutId + " not found"));

    workoutPlanRepository.delete(workoutPlan);
  }
}
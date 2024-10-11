package source.code.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.model.Plan;
import source.code.model.Workout;
import source.code.model.WorkoutPlan;
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
          final WorkoutPlanRepository workoutPlanRepository,
          final WorkoutRepository workoutRepository,
          final PlanRepository planRepository) {
    this.workoutPlanRepository = workoutPlanRepository;
    this.workoutRepository = workoutRepository;
    this.planRepository = planRepository;
  }

  @Transactional
  public void saveWorkoutToPlan(int workoutId, int planId) {
    Plan plan = planRepository
            .findById(planId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Plan with id: " + planId + " not found"));

    Workout workout = workoutRepository
            .findById(workoutId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Workout with id: " + workoutId + " not found"));

    WorkoutPlan planWorkout =
            WorkoutPlan.createWithWorkoutPlan(workout, plan);
    workoutPlanRepository.save(planWorkout);
  }

  @Transactional
  public void deleteWorkoutFromPlan(int workoutId, int planId) {
    WorkoutPlan planWorkout = workoutPlanRepository
            .findByPlanIdAndWorkoutId(planId, workoutId)
            .orElseThrow(() -> new NoSuchElementException(
                    "PlanWorkout with plan id: " + planId
                            + " and workout id: " + workoutId + " not found"));

    workoutPlanRepository.delete(planWorkout);
  }
}
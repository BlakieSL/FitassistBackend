package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Models.Plan;
import com.example.simplefullstackproject.Models.Workout;
import com.example.simplefullstackproject.Models.WorkoutPlan;
import com.example.simplefullstackproject.Repositories.PlanRepository;
import com.example.simplefullstackproject.Repositories.WorkoutPlanRepository;
import com.example.simplefullstackproject.Repositories.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutRepository workoutRepository;
    private final PlanRepository planRepository;

    public WorkoutPlanService(
            final WorkoutPlanRepository workoutPlanRepository,
            final WorkoutRepository workoutRepository,
            final PlanRepository planRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutRepository = workoutRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public void addWorkoutToPlan(Integer workoutId, Integer planId) {
        Plan plan = planRepository
                .findById(planId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Plan with id: " + planId + " not found"));

        Workout workout = workoutRepository
                .findById(workoutId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout with id: " + workoutId + " not found"));

        WorkoutPlan planWorkout = new WorkoutPlan();
        planWorkout.setPlan(plan);
        planWorkout.setWorkout(workout);
        workoutPlanRepository.save(planWorkout);
    }

    @Transactional
    public void deleteWorkoutFromPlan(Integer workoutId, Integer planId) {
        WorkoutPlan planWorkout = workoutPlanRepository
                .findByPlanIdAndWorkoutId(planId, workoutId)
                .orElseThrow(() -> new NoSuchElementException(
                        "PlanWorkout with plan id: " + planId +
                                " and workout id: " + workoutId + " not found"));

        workoutPlanRepository.delete(planWorkout);
    }
}
package source.code.model.Workout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Plan.Plan;

@Entity
@Table(name = "workout_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "workout_id", nullable = false)
  private Workout workout;

  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  public static WorkoutPlan createWithWorkoutPlan(
          Workout workout, Plan plan) {

    WorkoutPlan workoutPlan = new WorkoutPlan();
    workoutPlan.setWorkout(workout);
    workoutPlan.setPlan(plan);

    return workoutPlan;
  }
}

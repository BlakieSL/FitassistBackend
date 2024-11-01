package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_target_muscle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTargetMuscle {
  public static final String TARGET_MUSCLE = "targetMuscle";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "target_muscle_id", nullable = false)
  private TargetMuscle targetMuscle;

  @NotNull
  @Column(nullable = false)
  private int priority;

  public static ExerciseTargetMuscle createWithTargetMuscle(
          TargetMuscle targetMuscle) {
    ExerciseTargetMuscle exerciseTargetMuscle = new ExerciseTargetMuscle();
    exerciseTargetMuscle.setTargetMuscle(targetMuscle);

    return exerciseTargetMuscle;
  }

  public static ExerciseTargetMuscle createWithIdAndExerciseAndTurgetMuscle(
          int id, Exercise exercise, TargetMuscle targetMuscle) {
    ExerciseTargetMuscle association = new ExerciseTargetMuscle();
    association.setId(id);
    association.setExercise(exercise);
    association.setTargetMuscle(targetMuscle);
    return association;
  }
}

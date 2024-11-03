package source.code.dto.Response.WorkoutSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Exercise.Exercise;
import source.code.model.Workout.Workout;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetResponseDto {
  private Integer id;
  private double weight;
  private int repetitions;
  private int workoutId;
  private int exerciseId;
}

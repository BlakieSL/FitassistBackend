package source.code.dto.Request.WorkoutSet;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetCreateDto {
  @NotNull
  private double weight;

  @NotNull
  private int repetitions;

  @NotNull
  private Integer workoutTypeId;

  @NotNull
  private Integer exerciseId;
}

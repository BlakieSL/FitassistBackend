package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetResponseDto {
  private Integer id;
  private double weight;
  private int repetitions;
  private Integer workoutTypeId;
  private Integer exerciseId;
}

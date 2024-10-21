package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponseDto {
  private Integer id;
  private String name;
  private int time;
  private Integer workoutTypeId;
}

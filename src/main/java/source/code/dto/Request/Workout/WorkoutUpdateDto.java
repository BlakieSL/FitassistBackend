package source.code.dto.Request.Workout;


import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutUpdateDto {
  private static final int NAME_MAX_LENGTH = 50;

  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @PositiveOrZero
  private int time;
}

package source.code.dto.Request.Workout;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class WorkoutCreateDto {
  private static final int NAME_MAX_LENGTH = 50;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @NotNull
  @PositiveOrZero
  private int time;


}

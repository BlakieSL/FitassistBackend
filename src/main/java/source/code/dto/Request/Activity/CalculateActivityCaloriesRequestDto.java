package source.code.dto.Request.Activity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateActivityCaloriesRequestDto {
  @NotNull
  private Integer userId;
  @NotNull
  @Positive
  private int time = 1;
}

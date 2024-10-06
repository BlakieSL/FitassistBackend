package source.code.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyFoodDto {
  @NotNull
  @Positive
  @Max(value = 1000)
  private int amount;
}

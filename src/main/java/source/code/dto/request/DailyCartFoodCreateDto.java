package source.code.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyCartFoodCreateDto {
  @NotNull
  @Positive
  @Max(value = 1000)
  private int amount;
}

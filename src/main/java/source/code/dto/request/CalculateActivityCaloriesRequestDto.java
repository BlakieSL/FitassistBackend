package source.code.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateActivityCaloriesRequestDto {
    @NotNull
    private Integer userId;
    @NotNull
    @Positive
    private int time = 1;
}

package source.code.dto.request.workoutSet;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class    WorkoutSetCreateDto {
    @NotNull
    private BigDecimal weight;

    @NotNull
    private BigDecimal repetitions;

    @NotNull
    private Integer workoutSetGroupId;

    @NotNull
    private Integer exerciseId;
}

package source.code.dto.request.workoutSet;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetUpdateDto {
    @Positive
    private BigDecimal weight;
    @Positive
    private BigDecimal repetitions;
    private Integer workoutSetGroupId;
    private Integer exerciseId;
}

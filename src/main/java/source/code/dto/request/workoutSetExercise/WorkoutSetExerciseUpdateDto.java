package source.code.dto.request.workoutSetExercise;

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
public class WorkoutSetExerciseUpdateDto {
    @Positive
    private BigDecimal weight;
    @Positive
    private BigDecimal repetitions;
    private Integer workoutSetId;
    private Integer exerciseId;
    @Positive
    private Integer orderIndex;
}

package source.code.dto.request.workoutSetExercise;

import jakarta.validation.constraints.NotNull;
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
public class WorkoutSetExerciseCreateDto {
    @NotNull
    private BigDecimal weight;

    @NotNull
    private BigDecimal repetitions;

    @NotNull
    private Integer workoutSetId;

    @NotNull
    private Integer exerciseId;

    @NotNull
    @Positive
    private Integer orderIndex;
}

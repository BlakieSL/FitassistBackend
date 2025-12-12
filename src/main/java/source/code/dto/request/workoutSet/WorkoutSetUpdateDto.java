package source.code.dto.request.workoutSet;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetUpdateDto {
    @Positive
    private Short orderIndex;
    @Positive
    private Short restSeconds;
    private Integer workoutId;
}

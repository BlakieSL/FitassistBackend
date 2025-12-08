package source.code.dto.request.workoutSet;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetCreateDto {
    @NotNull
    private Integer orderIndex;

    @NotNull
    private Integer restSeconds;

    @NotNull
    private Integer workoutId;
}

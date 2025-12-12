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
    private Short orderIndex;

    @NotNull
    private Short restSeconds;

    @NotNull
    private Integer workoutId;
}

package source.code.dto.request.workoutSetGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetGroupUpdateDto {
    private Integer orderIndex;
    private Integer restSeconds;
    private Integer workoutId;
}

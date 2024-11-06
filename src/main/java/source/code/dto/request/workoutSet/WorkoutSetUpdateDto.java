package source.code.dto.request.workoutSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetUpdateDto {
    private double weight;
    private int repetitions;
    private int workoutId;
    private int exerciseId;
}

package source.code.dto.Response.workoutSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetResponseDto {
    private Integer id;
    private double weight;
    private int repetitions;
    private int workoutId;
    private int exerciseId;
}

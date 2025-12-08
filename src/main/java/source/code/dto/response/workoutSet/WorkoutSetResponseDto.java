package source.code.dto.response.workoutSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.workoutSetExercise.WorkoutSetExerciseResponseDto;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetResponseDto implements Serializable {
    private int id;
    private int orderIndex;
    private int restSeconds;
    private List<WorkoutSetExerciseResponseDto> workoutSetExercises;
}

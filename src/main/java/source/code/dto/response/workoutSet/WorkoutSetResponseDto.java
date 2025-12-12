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
    private Integer id;
    private Short orderIndex;
    private Short restSeconds;
    private List<WorkoutSetExerciseResponseDto> workoutSetExercises;
}

package source.code.dto.response.workoutSetGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetGroupResponseDto implements Serializable {
    private int id;
    private int orderIndex;
    private int restSeconds;
    private List<WorkoutSetResponseDto> workoutSets;
}
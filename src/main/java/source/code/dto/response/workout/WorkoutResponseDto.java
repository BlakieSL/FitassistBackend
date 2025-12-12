package source.code.dto.response.workout;

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
public class WorkoutResponseDto implements Serializable {
    private Integer id;
    private String name;
    private Short duration;
    private Byte restDaysAfter;
    private Integer weekIndex;
    private Integer dayOfWeekIndex;
    private List<WorkoutSetResponseDto> workoutSets;
}

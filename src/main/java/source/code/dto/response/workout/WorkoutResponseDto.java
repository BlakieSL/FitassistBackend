package source.code.dto.response.workout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.workoutSet.WorkoutSetResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutResponseDto implements Serializable {
    private int id;
    private String name;
    private BigDecimal duration;
    private int restDaysAfter;
    private Integer weekIndex;
    private Integer dayOfWeekIndex;
    private List<WorkoutSetResponseDto> workoutSets;
}

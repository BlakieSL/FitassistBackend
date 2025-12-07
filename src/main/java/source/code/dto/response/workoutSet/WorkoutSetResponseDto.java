package source.code.dto.response.workoutSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetResponseDto implements Serializable {
    private int id;
    private int orderIndex;
    private BigDecimal weight;
    private BigDecimal repetitions;

    private int exerciseId;
    private String exerciseName;
}

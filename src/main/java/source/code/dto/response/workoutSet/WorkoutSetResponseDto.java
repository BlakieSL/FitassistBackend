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
    private Integer id;
    private BigDecimal weight;
    private BigDecimal repetitions;
    private Integer workoutSetGroupId;
    private Integer exerciseId;
}

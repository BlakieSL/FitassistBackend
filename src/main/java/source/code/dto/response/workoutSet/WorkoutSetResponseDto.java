package source.code.dto.response.workoutSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetResponseDto {
    private Integer id;
    private BigDecimal weight;
    private BigDecimal repetitions;
    private Integer workoutSetGroupId;
    private Integer exerciseId;
}

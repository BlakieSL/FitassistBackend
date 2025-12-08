package source.code.dto.request.workout;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutUpdateDto {
    private static final int NAME_MAX_LENGTH = 50;

    @Size(max = NAME_MAX_LENGTH)
    private String name;

    @PositiveOrZero
    private BigDecimal duration;

    @Positive
    private Integer orderIndex;
}

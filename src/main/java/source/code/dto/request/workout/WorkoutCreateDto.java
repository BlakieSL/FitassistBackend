package source.code.dto.request.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class WorkoutCreateDto {
    private static final int NAME_MAX_LENGTH = 50;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal duration;

    @NotNull
    private Integer planId;
}

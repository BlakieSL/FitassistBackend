package source.code.dto.request.workoutSetExercise;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetExerciseNestedCreateDto {

	@NotNull
	@Positive
	private BigDecimal weight;

	@NotNull
	@Positive
	private Short repetitions;

	@NotNull
	private Integer exerciseId;

	@NotNull
	@Positive
	private Short orderIndex;

}

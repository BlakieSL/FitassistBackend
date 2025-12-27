package source.code.dto.request.workoutSetExercise;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetExerciseCreateDto {

	@NotNull
	private BigDecimal weight;

	@NotNull
	@Positive
	private Short repetitions;

	@NotNull
	private Integer workoutSetId;

	@NotNull
	private Integer exerciseId;

	@NotNull
	@Positive
	private Short orderIndex;

}

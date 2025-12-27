package source.code.dto.request.workoutSetExercise;

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
public class WorkoutSetExerciseUpdateDto {

	@Positive
	private BigDecimal weight;

	@Positive
	private Short repetitions;

	private Integer workoutSetId;

	private Integer exerciseId;

	@Positive
	private Short orderIndex;

}

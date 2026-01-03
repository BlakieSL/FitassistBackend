package source.code.dto.request.workoutSetExercise;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetExerciseNestedUpdateDto {

	private Integer id;

	@Positive
	private BigDecimal weight;

	@Positive
	private Short repetitions;

	private Integer exerciseId;

	@Positive
	private Short orderIndex;

}

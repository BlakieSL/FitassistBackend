package source.code.dto.response.workoutSetExercise;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetExerciseResponseDto implements Serializable {

	private Integer id;

	private Short orderIndex;

	private BigDecimal weight;

	private Short repetitions;

	private Integer exerciseId;

	private String exerciseName;

}

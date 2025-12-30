package source.code.dto.request.workout;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.request.workoutSet.WorkoutSetCreateDto;
import source.code.dto.request.workoutSet.WorkoutSetNestedCreateDto;

import java.util.List;

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
	private Short duration;

	@NotNull
	private Integer planId;

	@NotNull
	@Positive
	private Short orderIndex;

	@NotNull
	@PositiveOrZero
	private Byte restDaysAfter;

	@Valid
	private List<WorkoutSetNestedCreateDto> workoutSets;

}

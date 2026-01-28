package com.fitassist.backend.dto.request.plan.workout;

import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutNestedCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@PositiveOrZero
	@Max(480)
	private Short duration;

	@NotNull
	@Positive
	@Max(365)
	private Short orderIndex;

	@NotNull
	@PositiveOrZero
	@Max(7)
	private Byte restDaysAfter;

	@Valid
	private List<WorkoutSetNestedCreateDto> workoutSets;

}

package com.fitassist.backend.dto.request.plan.workout;

import com.fitassist.backend.dto.request.plan.workoutSet.WorkoutSetNestedUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutNestedUpdateDto {

	private Integer id;

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@PositiveOrZero
	private Short duration;

	@Positive
	private Short orderIndex;

	@PositiveOrZero
	private Byte restDaysAfter;

	@Valid
	private List<WorkoutSetNestedUpdateDto> workoutSets;

}

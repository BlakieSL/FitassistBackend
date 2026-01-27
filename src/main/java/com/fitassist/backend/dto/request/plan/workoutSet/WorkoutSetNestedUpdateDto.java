package com.fitassist.backend.dto.request.plan.workoutSet;

import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetNestedUpdateDto {

	private Integer id;

	@Positive
	private Short orderIndex;

	@PositiveOrZero
	private Short restSeconds;

	@Valid
	private List<WorkoutSetExerciseNestedUpdateDto> workoutSetExercises;

}

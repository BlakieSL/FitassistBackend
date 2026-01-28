package com.fitassist.backend.dto.request.plan.workoutSet;

import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
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
public class WorkoutSetNestedCreateDto {

	@NotNull
	@Positive
	@Max(50)
	private Short orderIndex;

	@NotNull
	@PositiveOrZero
	@Max(600)
	private Short restSeconds;

	@Valid
	private List<WorkoutSetExerciseNestedCreateDto> workoutSetExercises;

}

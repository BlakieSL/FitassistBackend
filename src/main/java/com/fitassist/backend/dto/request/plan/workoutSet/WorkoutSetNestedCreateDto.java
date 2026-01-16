package com.fitassist.backend.dto.request.plan.workoutSet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedCreateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetNestedCreateDto {

	@NotNull
	private Short orderIndex;

	@NotNull
	private Short restSeconds;

	@Valid
	private List<WorkoutSetExerciseNestedCreateDto> workoutSetExercises;

}

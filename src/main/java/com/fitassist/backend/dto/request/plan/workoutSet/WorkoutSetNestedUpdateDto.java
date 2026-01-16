package com.fitassist.backend.dto.request.plan.workoutSet;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.request.plan.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetNestedUpdateDto {

	private Integer id;

	private Short orderIndex;

	private Short restSeconds;

	@Valid
	private List<WorkoutSetExerciseNestedUpdateDto> workoutSetExercises;

}

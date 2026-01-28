package com.fitassist.backend.dto.request.plan.workoutSetExercise;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetExerciseNestedCreateDto {

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 1)
	private BigDecimal weight;

	@NotNull
	@Positive
	@Max(100)
	private Short repetitions;

	@NotNull
	@Positive
	@Max(20)
	private Short orderIndex;

	@NotNull
	private int exerciseId;

}

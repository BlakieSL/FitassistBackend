package com.fitassist.backend.dto.request.plan.workoutSetExercise;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

	@PositiveOrZero
	@Digits(integer = 3, fraction = 1)
	private BigDecimal weight;

	@Positive
	@Max(100)
	private Short repetitions;

	@Positive
	@Max(20)
	private Short orderIndex;

	private Integer exerciseId;

}

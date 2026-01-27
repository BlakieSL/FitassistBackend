package com.fitassist.backend.dto.request.plan.workoutSetExercise;

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
	private BigDecimal weight;

	@Positive
	private Short repetitions;

	@Positive
	private Short orderIndex;

	private Integer exerciseId;

}

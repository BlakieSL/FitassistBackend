package com.fitassist.backend.dto.request.plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedCreateDto;
import com.fitassist.backend.dto.request.text.TextCreateDto;
import com.fitassist.backend.model.plan.PlanStructureType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanCreateDto {

	private static final int NAME_MAX_LENGTH = 100;

	private static final int DESCRIPTION_MAX_LENGTH = 255;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = DESCRIPTION_MAX_LENGTH)
	private String description;

	private Boolean isPublic = false;

	@NotNull
	private PlanStructureType planStructureType;

	private List<Integer> categoryIds;

	@Valid
	private List<TextCreateDto> instructions;

	@Valid
	private List<WorkoutNestedCreateDto> workouts;

}

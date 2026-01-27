package com.fitassist.backend.dto.request.plan;

import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedCreateDto;
import com.fitassist.backend.dto.request.text.TextCreateDto;
import com.fitassist.backend.model.plan.PlanStructureType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PlanCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
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

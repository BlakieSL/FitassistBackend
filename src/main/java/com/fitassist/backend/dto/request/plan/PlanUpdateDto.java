package com.fitassist.backend.dto.request.plan;

import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedUpdateDto;
import com.fitassist.backend.dto.request.text.TextUpdateDto;
import com.fitassist.backend.model.plan.PlanStructureType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class PlanUpdateDto {

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@Size(max = TEXT_MAX_LENGTH)
	private String description;

	private Boolean isPublic;

	private PlanStructureType planStructureType;

	@NotEmpty
	private List<Integer> categoryIds;

	private List<TextUpdateDto> instructions;

	@Valid
	private List<WorkoutNestedUpdateDto> workouts;

}

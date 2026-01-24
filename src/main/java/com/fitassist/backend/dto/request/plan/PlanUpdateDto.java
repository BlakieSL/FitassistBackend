package com.fitassist.backend.dto.request.plan;

import com.fitassist.backend.dto.request.plan.workout.WorkoutNestedUpdateDto;
import com.fitassist.backend.dto.request.text.TextUpdateDto;
import com.fitassist.backend.model.plan.PlanStructureType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanUpdateDto {

	private static final int NAME_MAX_LENGTH = 100;

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	private String description;

	private Boolean isPublic;

	private PlanStructureType planStructureType;

	private List<Integer> categoryIds;

	private List<TextUpdateDto> instructions;

	@Valid
	private List<WorkoutNestedUpdateDto> workouts;

}

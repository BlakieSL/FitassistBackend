package com.fitassist.backend.dto.request.exercise;

import com.fitassist.backend.dto.request.text.TextUpdateDto;
import jakarta.validation.Valid;
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
public class ExerciseUpdateDto {

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@Size(max = TEXT_MAX_LENGTH)
	private String description;

	private Integer equipmentId;

	private Integer expertiseLevelId;

	private Integer mechanicsTypeId;

	private Integer forceTypeId;

	@Size(min = 1)
	private List<Integer> targetMuscleIds;

	@Valid
	private List<TextUpdateDto> instructions;

	@Valid
	private List<TextUpdateDto> tips;

}

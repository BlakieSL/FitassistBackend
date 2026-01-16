package com.fitassist.backend.dto.request.exercise;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.request.text.TextUpdateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseUpdateDto {

	private static final int MAX_NAME_LENGTH = 100;

	private static final int MAX_DESCRIPTION_LENGTH = 255;

	@Size(max = MAX_NAME_LENGTH)
	private String name;

	@Size(max = MAX_DESCRIPTION_LENGTH)
	private String description;

	private Integer equipmentId;

	private Integer expertiseLevelId;

	private Integer mechanicsTypeId;

	private Integer forceTypeId;

	private List<Integer> targetMuscleIds;

	private List<TextUpdateDto> instructions;

	private List<TextUpdateDto> tips;

}

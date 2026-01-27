package com.fitassist.backend.dto.request.exercise;

import com.fitassist.backend.dto.request.text.TextCreateDto;
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
public class ExerciseCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	private String description;

	@NotNull
	private int expertiseLevelId;

	private Integer equipmentId;

	private Integer mechanicsTypeId;

	private Integer forceTypeId;

	private List<Integer> targetMusclesIds;

	private List<TextCreateDto> instructions;

	private List<TextCreateDto> tips;

}

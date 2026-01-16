package com.fitassist.backend.dto.request.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.request.text.TextCreateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseCreateDto {

	private static final int MAX_NAME_LENGTH = 100;

	private static final int MAX_DESCRIPTION_LENGTH = 255;

	@NotBlank
	@Size(max = MAX_NAME_LENGTH)
	private String name;

	@NotBlank
	@Size(max = MAX_DESCRIPTION_LENGTH)
	private String description;

	@NotNull
	private int equipmentId;

	@NotNull
	private int expertiseLevelId;

	@NotNull
	private int mechanicsTypeId;

	@NotNull
	private int forceTypeId;

	private List<Integer> targetMusclesIds;

	private List<TextCreateDto> instructions;

	private List<TextCreateDto> tips;

}

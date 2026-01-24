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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseCreateDto {

	private static final int MAX_NAME_LENGTH = 100;

	@NotBlank
	@Size(max = MAX_NAME_LENGTH)
	private String name;

	@NotBlank
	private String description;

	private Integer equipmentId;

	@NotNull
	private int expertiseLevelId;

	private Integer mechanicsTypeId;

	private Integer forceTypeId;

	private List<Integer> targetMusclesIds;

	private List<TextCreateDto> instructions;

	private List<TextCreateDto> tips;

}

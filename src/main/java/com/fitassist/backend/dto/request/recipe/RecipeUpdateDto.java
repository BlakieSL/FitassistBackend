package com.fitassist.backend.dto.request.recipe;

import com.fitassist.backend.dto.request.text.TextUpdateDto;
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
public class RecipeUpdateDto {

	private static final int NAME_MAX_LENGTH = 100;

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	private String description;

	private Short minutesToPrepare;

	private Boolean isPublic;

	private List<Integer> categoryIds;

	private List<TextUpdateDto> instructions;

}

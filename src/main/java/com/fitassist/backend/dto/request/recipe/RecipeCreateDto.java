package com.fitassist.backend.dto.request.recipe;

import com.fitassist.backend.dto.request.text.TextCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class RecipeCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	private String description;

	@NotNull
	@Positive
	private Short minutesToPrepare;

	private Boolean isPublic = false;

	@NotEmpty
	private List<Integer> categoryIds;

	@Valid
	private List<TextCreateDto> instructions;

}

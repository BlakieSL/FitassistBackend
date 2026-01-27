package com.fitassist.backend.dto.request.recipe;

import com.fitassist.backend.dto.request.text.TextUpdateDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
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
public class RecipeUpdateDto {

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@Size(max = TEXT_MAX_LENGTH)
	private String description;

	@Positive
	private Short minutesToPrepare;

	private Boolean isPublic;

	@Size(min = 1)
	private List<Integer> categoryIds;

	private List<TextUpdateDto> instructions;

}

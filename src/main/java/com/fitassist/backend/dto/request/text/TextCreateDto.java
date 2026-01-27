package com.fitassist.backend.dto.request.text;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextCreateDto {

	@NotNull
	@Positive
	private Short orderIndex;

	@Size(max = NAME_MAX_LENGTH)
	private String title;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	private String text;

}

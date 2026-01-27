package com.fitassist.backend.dto.request.text;

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
public class TextUpdateDto {

	private Integer id;

	@Positive
	private Short orderIndex;

	@Size(max = NAME_MAX_LENGTH)
	private String title;

	@Size(max = TEXT_MAX_LENGTH)
	private String text;

}

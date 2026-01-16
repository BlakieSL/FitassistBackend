package com.fitassist.backend.dto.request.text;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextCreateDto {

	@NotNull
	private Short orderIndex;

	private String title;

	@NotBlank
	private String text;

}

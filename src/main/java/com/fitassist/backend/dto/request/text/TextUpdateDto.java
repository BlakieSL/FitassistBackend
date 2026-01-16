package com.fitassist.backend.dto.request.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextUpdateDto {

	private Integer id;

	private Short orderIndex;

	private String text;

	private String title;

}

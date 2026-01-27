package com.fitassist.backend.dto.response.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextResponseDto implements Serializable {

	private Integer id;

	private Short orderIndex;

	private String text;

	private String title;

}

package com.fitassist.backend.dto.response.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenericSearchResponseDto extends SearchResponseDto {

	public GenericSearchResponseDto(Integer id, String name, String type) {
		super(id, name, type);
	}

}
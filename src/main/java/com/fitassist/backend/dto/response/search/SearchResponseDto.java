package com.fitassist.backend.dto.response.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SearchResponseDto implements Serializable {

	private Integer id;

	private String name;

	private String type;

}

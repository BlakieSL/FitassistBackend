package com.fitassist.backend.dto.response.search;

import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExerciseSearchResponseDto extends SearchResponseDto {

	private String firstImageUrl;

	private CategoryResponseDto expertiseLevel;

	private CategoryResponseDto equipment;

	private CategoryResponseDto mechanicsType;

	private CategoryResponseDto forceType;

	public ExerciseSearchResponseDto(Integer id, String name, String firstImageUrl, CategoryResponseDto expertiseLevel,
			CategoryResponseDto equipment, CategoryResponseDto mechanicsType, CategoryResponseDto forceType) {
		super(id, name, "Exercise");
		this.firstImageUrl = firstImageUrl;
		this.expertiseLevel = expertiseLevel;
		this.equipment = equipment;
		this.mechanicsType = mechanicsType;
		this.forceType = forceType;
	}

}

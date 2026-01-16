package com.fitassist.backend.dto.response.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;

@Getter
@Setter
@NoArgsConstructor
public class FoodSearchResponseDto extends SearchResponseDto {

	private FoodMacros foodMacros;

	private String firstImageUrl;

	private CategoryResponseDto category;

	public FoodSearchResponseDto(Integer id, String name, FoodMacros foodMacros, String firstImageUrl,
			CategoryResponseDto category) {
		super(id, name, "Food");
		this.foodMacros = foodMacros;
		this.firstImageUrl = firstImageUrl;
		this.category = category;
	}

}

package com.fitassist.backend.dto.response.food;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponseDto {

	private Integer id;

	private String name;

	private FoodMacros foodMacros;

	private CategoryResponseDto category;

	private String firstImageUrl;

}

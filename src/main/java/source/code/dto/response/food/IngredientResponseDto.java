package source.code.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.response.category.CategoryResponseDto;

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

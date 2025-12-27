package source.code.dto.response.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.response.category.CategoryResponseDto;

@Getter
@Setter
@NoArgsConstructor
public class FoodSearchResponseDto extends SearchResponseDto {

	private FoodMacros macros;

	private String firstImageUrl;

	private CategoryResponseDto category;

	public FoodSearchResponseDto(Integer id, String name, FoodMacros macros, String firstImageUrl,
			CategoryResponseDto category) {
		super(id, name, "Food");
		this.macros = macros;
		this.firstImageUrl = firstImageUrl;
		this.category = category;
	}

}

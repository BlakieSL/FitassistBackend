package source.code.dto.response.search;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;

@Getter
@Setter
@NoArgsConstructor
public class ActivitySearchResponseDto extends SearchResponseDto {

	private BigDecimal met;

	private String firstImageUrl;

	private CategoryResponseDto category;

	public ActivitySearchResponseDto(Integer id, String name, BigDecimal met, String firstImageUrl,
			CategoryResponseDto category) {
		super(id, name, "Activity");
		this.met = met;
		this.firstImageUrl = firstImageUrl;
		this.category = category;
	}

}

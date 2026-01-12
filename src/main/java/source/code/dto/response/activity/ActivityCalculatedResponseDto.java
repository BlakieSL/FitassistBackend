package source.code.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCalculatedResponseDto implements Serializable {

	private Integer dailyItemId;

	private Integer id;

	private String name;

	private BigDecimal met;

	private CategoryResponseDto category;

	private int caloriesBurned;

	private int time;

	private BigDecimal weight;

}

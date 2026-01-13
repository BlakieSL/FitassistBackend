package source.code.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportStats {

	private BigDecimal avgCaloriesConsumed;

	private BigDecimal maxCaloriesConsumed;

	private BigDecimal minCaloriesConsumed;

	private BigDecimal avgCaloriesBurned;

	private BigDecimal maxCaloriesBurned;

	private BigDecimal minCaloriesBurned;

	private BigDecimal avgNetCalories;

	private BigDecimal maxNetCalories;

	private BigDecimal minNetCalories;

}

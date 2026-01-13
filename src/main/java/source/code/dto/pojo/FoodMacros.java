package source.code.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodMacros implements Serializable {

	private BigDecimal calories;

	private BigDecimal protein;

	private BigDecimal fat;

	private BigDecimal carbohydrates;

	public static FoodMacros of(BigDecimal calories, BigDecimal protein, BigDecimal fat, BigDecimal carbohydrates) {
		return new FoodMacros(calories, protein, fat, carbohydrates);
	}

	public static FoodMacros zero() {
		return FoodMacros.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
	}

	public static FoodMacros withoutCalories(FoodMacros foodMacros) {
		return FoodMacros.of(null, foodMacros.getProtein(), foodMacros.getFat(), foodMacros.getCarbohydrates());
	}

}

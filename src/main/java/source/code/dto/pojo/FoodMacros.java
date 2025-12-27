package source.code.dto.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class FoodMacros implements Serializable {

	private BigDecimal calories;

	private BigDecimal protein;

	private BigDecimal fat;

	private BigDecimal carbohydrates;

}

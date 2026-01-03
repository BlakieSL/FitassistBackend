package source.code.dto.pojo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.food.IngredientResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFoodDto {

	private int id;

	private BigDecimal quantity;

	private IngredientResponseDto ingredient;

}

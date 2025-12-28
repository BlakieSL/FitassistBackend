package source.code.dto.request.recipe;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFoodCreateDto {

	@NotNull
	@Positive
	private BigDecimal quantity = BigDecimal.valueOf(100);

	@NotNull
	private List<Integer> foodIds;

}

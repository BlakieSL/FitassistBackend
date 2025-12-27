package source.code.dto.request.food;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateFoodMacrosRequestDto {

	@NotNull
	private BigDecimal quantity;

}

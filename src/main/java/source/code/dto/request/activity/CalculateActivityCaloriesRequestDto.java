package source.code.dto.request.activity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateActivityCaloriesRequestDto {

	@NotNull
	@Positive
	private int time = 1;

	private BigDecimal weight;

}

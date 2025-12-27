package source.code.dto.request.food;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyCartFoodCreateDto {

	@NotNull
	private BigDecimal quantity;

	@NotNull
	@PastOrPresent
	private LocalDate date;

}

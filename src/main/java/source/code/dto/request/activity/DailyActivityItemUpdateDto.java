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
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DailyActivityItemUpdateDto {

	@NotNull
	@Positive
	private Short time;

	@Positive
	private BigDecimal weight;

}

package source.code.dto.request.activity;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityUpdateDto {

	private static final int NAME_MAX_LENGTH = 50;

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@Positive
	private BigDecimal met;

	private Integer categoryId;

}

package source.code.dto.request.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCreateDto {

	private static final int NAME_MAX_LENGTH = 50;

	@Size(max = NAME_MAX_LENGTH)
	@NotBlank
	private String name;

	@NotNull
	@Positive
	private BigDecimal met;

	@NotNull
	private int categoryId;

}

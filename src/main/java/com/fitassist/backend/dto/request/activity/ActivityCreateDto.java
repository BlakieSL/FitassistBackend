package com.fitassist.backend.dto.request.activity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCreateDto {

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@Positive
	@Min(1)
	@Max(25)
	@Digits(integer = 2, fraction = 1)
	private BigDecimal met;

	@NotNull
	private int categoryId;

}

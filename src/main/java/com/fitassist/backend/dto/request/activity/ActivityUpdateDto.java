package com.fitassist.backend.dto.request.activity;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class ActivityUpdateDto {

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@Positive
	private BigDecimal met;

	private Integer categoryId;

}

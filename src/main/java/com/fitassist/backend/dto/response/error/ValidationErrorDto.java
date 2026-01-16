package com.fitassist.backend.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorDto {

	private String field;

	private String defaultMessage;

}
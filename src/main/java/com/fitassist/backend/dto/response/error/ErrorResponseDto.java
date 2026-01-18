package com.fitassist.backend.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

	private int status;

	private String message;

	private List<ValidationErrorDto> errors;

	public ErrorResponseDto(int status, String message) {
		this(status, message, null);
	}

}
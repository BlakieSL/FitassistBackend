package com.fitassist.backend.exception;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.response.error.ErrorResponseDto;
import com.fitassist.backend.dto.response.error.ValidationErrorDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotSupportedInteractionTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleNotSupportedInteractionTypeException(NotSupportedInteractionTypeException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(WeightRequiredException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleWeightRequiredException(WeightRequiredException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(InvalidFilterKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleInvalidFilterKeyException(InvalidFilterKeyException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(InvalidFilterValueException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleInvalidFilterValueException(InvalidFilterValueException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(InvalidFilterOperationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleInvalidFilterOperationException(InvalidFilterOperationException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponseDto handleNoSuchElementException(NoSuchElementException e) {
		return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(RecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponseDto handleRecordNotFoundException(RecordNotFoundException e) {
		return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponseDto handleIOException(IOException e) {
		return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}

	@ExceptionHandler(JacksonException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponseDto handleJacksonException(JacksonException e) {
		return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}

	@ExceptionHandler(FileProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponseDto handleFileProcessingException(FileProcessingException e) {
		return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		List<ValidationErrorDto> errors = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> new ValidationErrorDto(error.getField(), error.getDefaultMessage()))
			.collect(Collectors.toList());
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleConstraintViolationException(ConstraintViolationException e) {
		List<ValidationErrorDto> errors = e.getConstraintViolations()
			.stream()
			.map(v -> new ValidationErrorDto(v.getPropertyPath().toString(), v.getMessage()))
			.toList();
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		String message = "Invalid value for parameter '" + e.getName() + "'. Expected type: "
				+ Objects.requireNonNull(e.getRequiredType()).getSimpleName();
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), message);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Malformed JSON request");
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorResponseDto handleAccessDeniedException(AccessDeniedException e) {
		return new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), "Access is denied");
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponseDto handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
		return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	@ExceptionHandler(JwtAuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ErrorResponseDto handleJwtAuthenticationException(JwtAuthenticationException e) {
		return new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
	}

	@ExceptionHandler(NotUniqueRecordException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorResponseDto handleNotUniqueRecordException(NotUniqueRecordException e) {
		return new ErrorResponseDto(HttpStatus.CONFLICT.value(), e.getMessage());
	}

	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponseDto handleNullPointerException(NullPointerException e) {
		log.error("NullPointerException occurred", e);
		return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An unexpected error occurred. Please contact support.");
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponseDto handleIllegalStateException(IllegalStateException e) {
		log.error("IllegalStateException occurred", e);
		return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error occurred");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponseDto handleException(Exception e) {
		log.error("Unhandled exception occurred", e);
		return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An unexpected error occurred. Please contact support.");
	}

}

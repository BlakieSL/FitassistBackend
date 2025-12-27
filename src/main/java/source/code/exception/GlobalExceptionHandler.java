package source.code.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ConflictDeletionException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public String handleConflictDeletionException(ConflictDeletionException e) {
		return e.getMessage();
	}

	@ExceptionHandler(NotSupportedInteractionTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleNotSupportedInteractionTypeException(NotSupportedInteractionTypeException e) {
		return e.getMessage();
	}

	@ExceptionHandler(WeightRequiredException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleWeightRequiredException(WeightRequiredException e) {
		return e.getMessage();
	}

	@ExceptionHandler(InvalidFilterKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleInvalidFilterKeyException(InvalidFilterKeyException e) {
		return e.getMessage();
	}

	@ExceptionHandler(InvalidFilterValueException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleInvalidFilterValueException(InvalidFilterValueException e) {
		return e.getMessage();
	}

	@ExceptionHandler(InvalidFilterOperationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleInvalidFilterOperationException(InvalidFilterOperationException e) {
		return e.getMessage();
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public String handleNoSuchElementException(NoSuchElementException e) {
		return e.getMessage();
	}

	@ExceptionHandler(RecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public String handleRecordNotFoundException(RecordNotFoundException e) {
		return e.getMessage();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleIllegalArgumentException(IllegalArgumentException e) {
		return e.getMessage();
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleIOException(IOException e) {
		return e.getMessage();
	}

	@ExceptionHandler(JsonPatchException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleJsonPatchException(JsonPatchException e) {
		return e.getMessage();
	}

	@ExceptionHandler(JsonProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleJsonProcessingException(JsonProcessingException e) {
		return e.getMessage();
	}

	@ExceptionHandler(FileProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleFileProcessingException(FileProcessingException e) {
		return e.getMessage();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return e.getBindingResult().getAllErrors();
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		return String.format("Invalid value for parameter '%s'. Expected type: %s", e.getName(),
				Objects.requireNonNull(e.getRequiredType()).getSimpleName());
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public String handleAccessDeniedException(AccessDeniedException e) {
		return "Access is denied: " + e.getMessage();
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
		return e.getMessage();
	}

	@ExceptionHandler(JwtAuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public String handleJwtAuthenticationException(JwtAuthenticationException e) {
		return e.getMessage();
	}

	@ExceptionHandler(NotUniqueRecordException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public String handleNotUniqueRecordException(NotUniqueRecordException e) {
		return e.getMessage();
	}

	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleNullPointerException(NullPointerException e) {
		return "A required value was missing: " + e.getMessage();
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleIllegalStateException(IllegalStateException e) {
		return "An error occurred: " + e.getMessage();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleException(Exception e) {
		return "An unexpected error occurred: " + e.getMessage();
	}

}

package com.fitassist.backend.exception;

public class InvalidFilterOperationException extends RuntimeException {

	public InvalidFilterOperationException(String message) {
		super("Invalid filter operation: " + message);
	}

}

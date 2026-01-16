package com.fitassist.backend.exception;

public class InvalidFilterKeyException extends RuntimeException {

	public InvalidFilterKeyException(String message) {
		super("Invalid filter key: " + message);
	}

}

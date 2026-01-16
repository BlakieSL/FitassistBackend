package com.fitassist.backend.exception;

public class InvalidFilterValueException extends RuntimeException {

	public InvalidFilterValueException(String message) {
		super("Invalid filter value: " + message);
	}

}

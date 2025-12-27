package source.code.exception;

public class InvalidFilterKeyException extends RuntimeException {

	public InvalidFilterKeyException(String message) {
		super("Invalid filter key: " + message);
	}

}

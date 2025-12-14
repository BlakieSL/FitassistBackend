package source.code.exception;

public class InvalidFilterValueException extends RuntimeException {
    public InvalidFilterValueException(String message) {
        super("Invalid filter value: " + message);
    }
}

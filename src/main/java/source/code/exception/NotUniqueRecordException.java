package source.code.exception;

public class NotUniqueRecordException extends RuntimeException {
    public NotUniqueRecordException(String message) {
        super(message);
    }
}
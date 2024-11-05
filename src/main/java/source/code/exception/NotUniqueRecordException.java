package source.code.exception;

public class NotUniqueRecordException extends RuntimeException {
    public NotUniqueRecordException(String message) {
        super(message);
    }

    public static NotUniqueRecordException of(String message) {
        return new NotUniqueRecordException(message);
    }
}
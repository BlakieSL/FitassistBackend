package source.code.exception;

public class ConflictDeletionException extends LocalizedException {
    public <T> ConflictDeletionException(Class<T> entityClass, int id) {
        super(
                "ConflictDeletionException.message",
                null,
                entityClass.getSimpleName(),
                id
           );
    }
}

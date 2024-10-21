package source.code.exception;

import java.util.Arrays;

public class RecordNotFoundException extends RuntimeException{
  public <T> RecordNotFoundException(Class<T> entityClass, Object... identifiers) {
    super(entityClass.getSimpleName() + " not found for identifiers: " + Arrays.toString(identifiers));
  }
}

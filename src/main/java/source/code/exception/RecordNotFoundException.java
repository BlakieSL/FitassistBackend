package source.code.exception;

import java.util.Arrays;

public class RecordNotFoundException extends RuntimeException{
  public RecordNotFoundException(String entityName, Object... identifiers) {
    super(entityName + " not found for identifiers: " + Arrays.toString(identifiers));
  }
}

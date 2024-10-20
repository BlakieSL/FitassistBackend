package source.code.service.declaration.Helpers;

public interface ValidationService {
  <T> void validate(T dto, Class<?>... groups);
}

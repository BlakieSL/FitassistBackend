package source.code.service.declaration.helpers;

public interface ValidationService {
    <T> void validate(T dto, Class<?>... groups);
}

package source.code.service.Declaration.Helpers;

public interface ValidationService {
    <T> void validate(T dto, Class<?>... groups);
}

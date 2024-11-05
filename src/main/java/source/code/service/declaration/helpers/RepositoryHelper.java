package source.code.service.declaration.helpers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;

public interface RepositoryHelper {
    <T> T find(JpaRepository<T, Integer> repository, Class<T> entityType, int id);

    <T, R> List<R> findAll(JpaRepository<T, Integer> repository, Function<T, R> mapper);
}

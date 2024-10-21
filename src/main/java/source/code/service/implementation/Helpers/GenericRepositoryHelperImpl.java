package source.code.service.implementation.Helpers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import source.code.exception.RecordNotFoundException;
import source.code.service.declaration.Helpers.GenericRepositoryHelper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GenericRepositoryHelperImpl implements GenericRepositoryHelper {
  @Override
  public <T> T findById(JpaRepository<T, Integer> repository, Class<T> entityType, int id) {
    return repository.findById(id)
            .orElseThrow(() -> new RecordNotFoundException(entityType.getSimpleName(), id));
  }

  @Override
  public <T, R> List<R> findAll(JpaRepository<T, Integer> repository, Function<T,R> mapper) {
    return repository.findAll().stream()
            .map(mapper)
            .collect(Collectors.toList());
  }
}

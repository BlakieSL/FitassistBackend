package source.code.service.implementation.Helpers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import source.code.exception.RecordNotFoundException;
import source.code.service.declaration.Helpers.GenericRepositoryHelper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericRepositoryHelperImpl<T, R> implements GenericRepositoryHelper<T, R> {
  private final JpaRepository<T, Integer> repository;
  private final Class<T> entityType;

  public GenericRepositoryHelperImpl(JpaRepository<T, Integer> repository,
                                     Class<T> entityType) {
    this.repository = repository;
    this.entityType = entityType;
  }

  @Override
  public T findByIdOrThrow(int id) {
    return repository.findById(id)
            .orElseThrow(() -> new RecordNotFoundException(entityType.getSimpleName(), id));
  }

  @Override
  public List<R> findAll(Function<T,R> mapper) {
    return repository.findAll().stream()
            .map(mapper)
            .collect(Collectors.toList());
  }
}

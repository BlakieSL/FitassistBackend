package source.code.service.declaration.Helpers;

import java.util.List;
import java.util.function.Function;

public interface GenericRepositoryHelper<T, R> {
  T findByIdOrThrow(int id);

  List<R> findAll(Function<T, R> mapper);
}

package source.code.service.implementation.helpers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import source.code.exception.RecordNotFoundException;
import source.code.service.declaration.helpers.RepositoryHelper;

import java.util.List;
import java.util.function.Function;

@Component
public class RepositoryHelperImpl implements RepositoryHelper {

	@Override
	public <T> T find(JpaRepository<T, Integer> repository, Class<T> entityType, int id) {
		return repository.findById(id).orElseThrow(() -> RecordNotFoundException.of(entityType, id));
	}

	@Override
	public <T, R> List<R> findAll(JpaRepository<T, Integer> repository, Function<T, R> mapper) {
		return repository.findAll().stream().map(mapper).toList();
	}

}

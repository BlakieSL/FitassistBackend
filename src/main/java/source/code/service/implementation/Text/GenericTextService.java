package source.code.service.implementation.Text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.response.Text.BaseTextResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class GenericTextService<T, R, U, E extends JpaRepository<T, Integer>> {
  protected final ValidationService validationService;
  protected final JsonPatchService jsonPatchService;
  protected final ApplicationEventPublisher applicationEventPublisher;
  protected final E repository;
  protected final Function<T, R> toResponse;
  protected final BiConsumer<T, U> update;
  protected final Class<U> entityType;
  protected GenericTextService(ValidationService validationService,
                               JsonPatchService jsonPatchService,
                               ApplicationEventPublisher applicationEventPublisher,
                               E repository,
                               Function<T, R> toResponse,
                               BiConsumer<T, U> update,
                               Class<U> entityType) {
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.repository = repository;
    this.toResponse = toResponse;
    this.update = update;
    this.entityType = entityType;
  }

  @Transactional
  public void deleteText(int id) {
    T entity = findById(id);
    repository.delete(entity);
  }

  @Transactional
  public void updateText(int id, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    T entity = findById(id);
    U patched = applyPatch(entity, patch);

    validationService.validate(patched);

    update.accept(entity, patched);
    T saved = repository.save(entity);
  }

  public List<BaseTextResponseDto> getAllByExercise(int exerciseId) {
    return getAllByExerciseId(exerciseId).stream()
            .map(entity -> (BaseTextResponseDto) toResponse.apply(entity))
            .toList();
  }

  private T findById(int id) {
    return repository.findById(id)
            .orElseThrow(() -> new RecordNotFoundException(getClass(), id));
  }

   private <U> U applyPatch(T entity, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    R response = toResponse.apply(entity);
    return jsonPatchService.applyPatch(patch, response, (Class<U>) entityType);
  }

  protected abstract List<T> getAllByExerciseId(int exerciseId);
}

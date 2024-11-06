package source.code.service.implementation.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.Response.text.BaseTextResponseDto;
import source.code.event.events.Text.TextClearCacheEvent;
import source.code.event.events.Text.TextCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.text.TextCacheKeyGenerator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class GenericTextService<T, R, U, E extends JpaRepository<T, Integer>> {
    protected final ValidationService validationService;
    protected final JsonPatchService jsonPatchService;
    protected final TextCacheKeyGenerator<T> textCacheKeyGenerator;
    protected final CacheManager cacheManager;
    protected final ApplicationEventPublisher applicationEventPublisher;
    protected final E repository;
    protected final Function<T, R> toResponse;
    protected final BiConsumer<T, U> update;
    protected final Class<U> entityType;

    protected GenericTextService(ValidationService validationService,
                                 JsonPatchService jsonPatchService,
                                 TextCacheKeyGenerator<T> textCacheKeyGenerator,
                                 CacheManager cacheManager,
                                 ApplicationEventPublisher applicationEventPublisher,
                                 E repository,
                                 Function<T, R> toResponse,
                                 BiConsumer<T, U> update,
                                 Class<U> entityType) {
        this.validationService = validationService;
        this.jsonPatchService = jsonPatchService;
        this.textCacheKeyGenerator = textCacheKeyGenerator;
        this.cacheManager = cacheManager;
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

        applicationEventPublisher.publishEvent(TextClearCacheEvent.of(
                this,
                textCacheKeyGenerator.generateCacheKey(entity)));
    }

    @Transactional
    public void updateText(int id, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {

        T entity = findById(id);
        U patched = applyPatch(entity, patch);

        validationService.validate(patched);
        update.accept(entity, patched);
        T saved = repository.save(entity);

        applicationEventPublisher.publishEvent(TextClearCacheEvent.of(
                this,
                textCacheKeyGenerator.generateCacheKey(saved)));
    }

    public List<BaseTextResponseDto> getAllByParent(int exerciseId) {
        String cacheKey = textCacheKeyGenerator.generateCacheKeyForParent(exerciseId);

        return getCachedText(cacheKey)
                .orElseGet(() -> {
                    List<BaseTextResponseDto> responseDtos = getAllByParentId(exerciseId).stream()
                            .map(entity -> (BaseTextResponseDto) toResponse.apply(entity))
                            .toList();

                    applicationEventPublisher.publishEvent(TextCreateCacheEvent.of(
                            this,
                            cacheKey,
                            responseDtos)
                    );

                    return responseDtos;
                });

    }

    private T findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> RecordNotFoundException.of(getClass(), id));
    }

    private U applyPatch(T entity, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        R response = toResponse.apply(entity);
        return jsonPatchService.applyPatch(patch, response, entityType);
    }

    private Optional<List<BaseTextResponseDto>> getCachedText(String cacheKey) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache("allTextByParent"));
        Cache.ValueWrapper cachedValue = cache.get(cacheKey);

        return Optional.ofNullable(cachedValue)
                .map(value -> ((List<BaseTextResponseDto>) cachedValue.get()));
    }

    protected abstract List<T> getAllByParentId(int parentId);
}

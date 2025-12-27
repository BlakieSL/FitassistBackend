package source.code.service.implementation.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.request.category.CategoryUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Category.CategoryCreateCacheEvent;
import source.code.exception.ConflictDeletionException;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.category.BaseMapper;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;

public abstract class GenericCategoryService<T> {

	protected final ValidationService validationService;

	protected final JsonPatchService jsonPatchService;

	protected final CategoryCacheKeyGenerator<T> cacheKeyGenerator;

	protected final ApplicationEventPublisher applicationEventPublisher;

	protected final CacheManager cacheManager;

	protected final JpaRepository<T, Integer> repository;

	protected final BaseMapper<T> mapper;

	protected abstract boolean hasAssociatedEntities(int categoryId);

	protected abstract Class<T> getEntityClass();

	protected GenericCategoryService(ValidationService validationService, JsonPatchService jsonPatchService,
									 CategoryCacheKeyGenerator<T> cacheKeyGenerator, ApplicationEventPublisher applicationEventPublisher,
									 CacheManager cacheManager, JpaRepository<T, Integer> repository, BaseMapper<T> mapper) {
		this.validationService = validationService;
		this.jsonPatchService = jsonPatchService;
		this.cacheKeyGenerator = cacheKeyGenerator;
		this.applicationEventPublisher = applicationEventPublisher;
		this.cacheManager = cacheManager;
		this.repository = repository;
		this.mapper = mapper;
	}

	@Transactional
	public CategoryResponseDto createCategory(CategoryCreateDto request) {
		T category = mapper.toEntity(request);
		T savedCategory = repository.save(category);

		applicationEventPublisher.publishEvent(CategoryClearCacheEvent.of(this, cacheKeyGenerator.generateCacheKey()));
		return mapper.toResponseDto(savedCategory);
	}

	@Transactional
	public void updateCategory(int categoryId, JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException {
		T category = find(categoryId);
		CategoryUpdateDto patchedCategory = applyPatchToCategory(patch);

		validationService.validate(patchedCategory);
		mapper.updateEntityFromDto(category, patchedCategory);
		repository.save(category);

		applicationEventPublisher.publishEvent(CategoryClearCacheEvent.of(this, cacheKeyGenerator.generateCacheKey()));
	}

	@Transactional
	public void deleteCategory(int categoryId) {
		T category = find(categoryId);

		if (hasAssociatedEntities(categoryId)) {
			throw new ConflictDeletionException(getEntityClass(), categoryId);
		}

		repository.delete(category);

		applicationEventPublisher.publishEvent(CategoryClearCacheEvent.of(this, cacheKeyGenerator.generateCacheKey()));
	}

	public List<CategoryResponseDto> getAllCategories() {
		String cacheKey = cacheKeyGenerator.generateCacheKey();

		return getCachedCategories(cacheKey).orElseGet(() -> {
			List<CategoryResponseDto> categoryResponseDtos = repository.findAll()
				.stream()
				.map(mapper::toResponseDto)
				.toList();

			applicationEventPublisher.publishEvent(CategoryCreateCacheEvent.of(this, cacheKey, categoryResponseDtos));

			return categoryResponseDtos;
		});
	}

	public CategoryResponseDto getCategory(int categoryId) {
		return mapper.toResponseDto(find(categoryId));
	}

	private T find(int categoryId) {
		return repository.findById(categoryId)
			.orElseThrow(() -> RecordNotFoundException.of(getEntityClass(), categoryId));
	}

	private CategoryUpdateDto applyPatchToCategory(JsonMergePatch patch)
		throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, CategoryUpdateDto.class);
	}

	private Optional<List<CategoryResponseDto>> getCachedCategories(String cacheKey) {
		Cache cache = Objects.requireNonNull(cacheManager.getCache("allCategories"));
		Cache.ValueWrapper cachedValue = cache.get(cacheKey);

		try {
			return Optional.ofNullable(cachedValue).map(value -> (List<CategoryResponseDto>) value.get());
		} catch (ClassCastException exception) {
			return Optional.empty();
		}
	}

}

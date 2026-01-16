package com.fitassist.backend.service.implementation.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.dto.request.category.CategoryCreateDto;
import com.fitassist.backend.dto.request.category.CategoryUpdateDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.event.events.Category.CategoryClearCacheEvent;
import com.fitassist.backend.event.events.Category.CategoryCreateCacheEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.category.BaseMapper;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
		}
		catch (ClassCastException exception) {
			return Optional.empty();
		}
	}

}

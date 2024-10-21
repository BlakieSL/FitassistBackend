package source.code.service.implementation.Category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.cache.event.Category.CategoryClearCacheEvent;
import source.code.cache.event.Category.CategoryCreateCacheEvent;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.service.declaration.Helpers.GenericRepositoryHelper;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.mapper.Generics.BaseMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class GenericCategoryService<T> {
  protected final ValidationService validationService;
  protected final JsonPatchService jsonPatchService ;
  protected final ApplicationEventPublisher applicationEventPublisher;
  protected final CacheManager cacheManager;
  protected final JpaRepository<T, Integer> repository;
  protected final BaseMapper<T> mapper;
  protected GenericCategoryService(ValidationService validationService,
                                   JsonPatchServiceImpl jsonPatchService,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   CacheManager cacheManager,
                                   JpaRepository<T, Integer> repository,
                                   BaseMapper<T> mapper) {
    this.validationService = validationService;
    this.jsonPatchService = jsonPatchService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.cacheManager = cacheManager;
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional
  public CategoryResponseDto createCategory(CategoryCreateDto request) {
    T category = mapper.toEntity(request);
    T savedCategory = repository.save(category);

    applicationEventPublisher.publishEvent(new CategoryClearCacheEvent(this, getSubClassName()));
    return mapper.toResponseDto(savedCategory);
  }

  @Transactional
  public void updateCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    T category = getCategoryOrThrow(categoryId);
    CategoryUpdateDto patchedCategory = applyPatchToCategory(category, patch);

    validationService.validate(patchedCategory);
    mapper.updateEntityFromDto(category, patchedCategory);
    repository.save(category);

    applicationEventPublisher.publishEvent(new CategoryClearCacheEvent(this, getSubClassName()));
  }

  @Transactional
  public void deleteCategory(int categoryId) {
    T category = getCategoryOrThrow(categoryId);
    repository.delete(category);

    applicationEventPublisher.publishEvent(new CategoryClearCacheEvent(this, getSubClassName()));
  }


  public List<CategoryResponseDto> getAllCategories() {
    String cacheKey = getSubClassName();

    return getCachedCategories(cacheKey)
            .orElseGet(() -> {
              List<T> categories = repository.findAll();
              List<CategoryResponseDto> categoryResponseDtos = repository.findAll().stream()
                      .map(mapper::toResponseDto)
                      .collect(Collectors.toList());

              applicationEventPublisher.publishEvent(
                      new CategoryCreateCacheEvent(this, cacheKey, categoryResponseDtos));

              return categoryResponseDtos;
            });
  }

  public CategoryResponseDto getCategory(int categoryId) {
    T category = getCategoryOrThrow(categoryId);
    return mapper.toResponseDto(category);
  }

  private T getCategoryOrThrow(int categoryId) {
    return repository.findById(categoryId)
            .orElseThrow(() -> new RecordNotFoundException(getSubClassName(), categoryId));
  }

  private CategoryUpdateDto applyPatchToCategory(T category, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    CategoryResponseDto response = mapper.toResponseDto(category);
    return jsonPatchService.applyPatch(patch, response, CategoryUpdateDto.class);
  }

  private Optional<List<CategoryResponseDto>> getCachedCategories(String cacheKey) {
    Cache cache = cacheManager.getCache("allCategories");

    if (cache == null) {
      throw new IllegalStateException("Cache not available for: allCategories");
    }

    Cache.ValueWrapper cachedValue = cache.get(cacheKey);

    if (cachedValue != null) {
      return Optional.of((List<CategoryResponseDto>) cachedValue.get());
    }

    return Optional.empty();
  }

  private String getSubClassName() {
    return getClass().getSimpleName();
  }
}

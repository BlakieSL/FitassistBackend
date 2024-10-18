package source.code.service.implementation.Generics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Generics.BaseMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public abstract class GenericCategoryService<T> {
  protected final ValidationHelper validationHelper;
  protected final JsonPatchHelper jsonPatchHelper;
  protected final JpaRepository<T, Integer> repository;
  protected final BaseMapper<T> mapper;
  protected GenericCategoryService(ValidationHelper validationHelper,
                                   JsonPatchHelper jsonPatchHelper,
                                   JpaRepository<T, Integer> repository,
                                   BaseMapper<T> mapper) {
    this.validationHelper = validationHelper;
    this.jsonPatchHelper = jsonPatchHelper;
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional
  public CategoryResponseDto createCategory(CategoryCreateDto request) {
    T category = mapper.toEntity(request);
    T savedCategory = repository.save(category);
    return mapper.toResponseDto(savedCategory);
  }

  @Transactional
  public void updateCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    T category = getCategoryOrThrow(categoryId);
    CategoryUpdateDto patchedCategory = applyPatchToCategory(category, patch);

    validationHelper.validate(patchedCategory);
    mapper.updateEntityFromDto(category, patchedCategory);
    repository.save(category);
  }

  @Transactional
  public void deleteCategory(int categoryId) {
    T category = getCategoryOrThrow(categoryId);
    repository.delete(category);
  }

  public List<CategoryResponseDto> getAllCategories() {
    List<T> categories = repository.findAll();

    return categories.stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public CategoryResponseDto getCategory(int categoryId) {
    T category = getCategoryOrThrow(categoryId);
    return mapper.toResponseDto(category);
  }

  private CategoryUpdateDto applyPatchToCategory(T category, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    CategoryResponseDto response = mapper.toResponseDto(category);
    return jsonPatchHelper.applyPatch(patch, response, CategoryUpdateDto.class);
  }

  private T getCategoryOrThrow(int categoryId) {
    return repository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(
                    getClass().getSimpleName() + " with id: " + categoryId + " not found"));
  }
}

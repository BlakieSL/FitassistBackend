package source.code.service.implementation.Category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.request.Category.CategoryUpdateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Generics.BaseMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public abstract class GenericCategoryService<T> {
  protected final ValidationServiceImpl validationServiceImpl;
  protected final JsonPatchServiceImpl jsonPatchServiceImpl;
  protected final JpaRepository<T, Integer> repository;
  protected final BaseMapper<T> mapper;
  protected GenericCategoryService(ValidationServiceImpl validationServiceImpl,
                                   JsonPatchServiceImpl jsonPatchServiceImpl,
                                   JpaRepository<T, Integer> repository,
                                   BaseMapper<T> mapper) {
    this.validationServiceImpl = validationServiceImpl;
    this.jsonPatchServiceImpl = jsonPatchServiceImpl;
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

    validationServiceImpl.validate(patchedCategory);
    mapper.updateEntityFromDto(category, patchedCategory);
    repository.save(category);
  }

  @Transactional
  public void deleteCategory(int categoryId) {
    T category = getCategoryOrThrow(categoryId);
    repository.delete(category);
  }


  public List<CategoryResponseDto> getAllCategories() {

    System.out.println();
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
    return jsonPatchServiceImpl.applyPatch(patch, response, CategoryUpdateDto.class);
  }

  private T getCategoryOrThrow(int categoryId) {
    return repository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(
                    getSubClassName() + " with id: " + categoryId + " not found"));
  }

  private String getSubClassName() {
    System.out.println(getClass().getSimpleName());
    return getClass().getSimpleName();
  }
}

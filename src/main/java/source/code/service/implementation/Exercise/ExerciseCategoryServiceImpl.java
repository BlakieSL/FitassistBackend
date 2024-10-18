package source.code.service.implementation.Exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Exercise.ExerciseCategoryMapper;
import source.code.model.Exercise.ExerciseCategory;
import source.code.repository.ExerciseCategoryRepository;
import source.code.service.declaration.ExerciseCategoryService;
import source.code.service.implementation.Generics.GenericCategoryService;

import java.util.List;

@Service
public class ExerciseCategoryServiceImpl
        extends GenericCategoryService<ExerciseCategory>
        implements ExerciseCategoryService {

  protected ExerciseCategoryServiceImpl(ValidationHelper validationHelper,
                                        JsonPatchHelper jsonPatchHelper,
                                        ExerciseCategoryRepository repository,
                                        ExerciseCategoryMapper mapper) {
    super(validationHelper, jsonPatchHelper, repository, mapper);
  }

  @CacheEvict(value = "allExerciseCategories")
  @Override
  public CategoryResponseDto createExerciseCategory(CategoryCreateDto request) {
    return createCategory(request);
  }

  @CacheEvict(value = "allExerciseCategories")
  @Override
  public void updateExerciseCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    updateCategory(categoryId, patch);
  }

  @CacheEvict(value = "allExerciseCategories")
  @Override
  public void deleteExerciseCategory(int categoryId) {
    deleteCategory(categoryId);
  }

  @Cacheable(value = "allExerciseCategories")
  @Override
  public List<CategoryResponseDto> getAllExerciseCategories() {
    return getAllCategories();
  }

  @Override
  public CategoryResponseDto getExerciseCategory(int categoryId) {
    return getCategory(categoryId);
  }
}

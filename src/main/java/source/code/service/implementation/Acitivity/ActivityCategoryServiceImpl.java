package source.code.service.implementation.Acitivity;

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
import source.code.mapper.Activity.ActivityCategoryMapper;
import source.code.model.Activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.declaration.ActivityCategoryService;
import source.code.service.implementation.Generics.GenericCategoryService;

import java.util.List;
@Service
public class ActivityCategoryServiceImpl
        extends GenericCategoryService<ActivityCategory>
        implements ActivityCategoryService {


  protected ActivityCategoryServiceImpl(ValidationHelper validationHelper,
                                        JsonPatchHelper jsonPatchHelper,
                                        ActivityCategoryRepository repository,
                                        ActivityCategoryMapper mapper) {
    super(validationHelper, jsonPatchHelper, repository, mapper);
  }

  @CacheEvict(value = "allActivityCategories")
  @Override
  public CategoryResponseDto createActivityCategory(CategoryCreateDto request) {
    return createCategory(request);
  }

  @CacheEvict(value = "allActivityCategories")
  @Override
  public void updateActivityCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    updateCategory(categoryId, patch);
  }

  @CacheEvict(value = "allActivityCategories")
  @Override
  public void deleteActivityCategory(int categoryId) {
    deleteCategory(categoryId);
  }

  @Cacheable(value = "allActivityCategories")
  @Override
  public List<CategoryResponseDto> getAllActivityCategories() {
    return getAllCategories();
  }

  @Override
  public CategoryResponseDto getActivityCategory(int categoryId) {
    return getCategory(categoryId);
  }
}

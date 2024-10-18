package source.code.service.implementation.Food;

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
import source.code.mapper.Food.FoodCategoryMapper;
import source.code.model.Food.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import source.code.service.declaration.FoodCategoryService;
import source.code.service.implementation.Generics.GenericCategoryService;

import java.util.List;

@Service
public class FoodCategoryServiceImpl
        extends GenericCategoryService<FoodCategory>
        implements FoodCategoryService {

  protected FoodCategoryServiceImpl(ValidationHelper validationHelper,
                                    JsonPatchHelper jsonPatchHelper,
                                    FoodCategoryRepository repository,
                                    FoodCategoryMapper mapper) {
    super(validationHelper, jsonPatchHelper, repository, mapper);
  }

  @CacheEvict(value = "allFoodCategories")
  @Override
  public CategoryResponseDto createFoodCategory(CategoryCreateDto request) {
    return createCategory(request);
  }

  @CacheEvict(value = "allFoodCategories")
  @Override
  public void updateFoodCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    updateCategory(categoryId, patch);
  }

  @CacheEvict(value = "allFoodCategories")
  @Override
  public void deleteFoodCategory(int categoryId) {
    deleteCategory(categoryId);
  }

  @Cacheable(value = "allFoodCategories")
  @Override
  public List<CategoryResponseDto> getAllFoodCategories() {
    return getAllCategories();
  }

  @Override
  public CategoryResponseDto getFoodCategory(int categoryId) {
    return getFoodCategory(categoryId);
  }
}

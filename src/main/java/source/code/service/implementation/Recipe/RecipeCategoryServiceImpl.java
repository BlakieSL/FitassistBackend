package source.code.service.implementation.Recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.dto.response.RecipeCategoryResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Generics.BaseMapper;
import source.code.mapper.Recipe.RecipeCategoryMapper;
import source.code.model.Recipe.RecipeCategory;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.declaration.RecipeCategoryService;
import source.code.service.implementation.Generics.GenericCategoryService;

import java.util.List;

@Service
public class RecipeCategoryServiceImpl extends GenericCategoryService<RecipeCategory>
        implements RecipeCategoryService {

  protected RecipeCategoryServiceImpl(ValidationHelper validationHelper,
                                      JsonPatchHelper jsonPatchHelper,
                                      RecipeCategoryRepository repository,
                                      RecipeCategoryMapper mapper) {
    super(validationHelper, jsonPatchHelper, repository, mapper);
  }

  @CacheEvict(value = "allRecipeCategories")
  @Override
  public CategoryResponseDto createRecipeCategory(CategoryCreateDto request) {
    return createCategory(request);
  }

  @CacheEvict(value = "allRecipeCategories")
  @Override
  public void updateRecipeCategory(int categoryId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
    updateCategory(categoryId, patch);
  }

  @CacheEvict(value = "allRecipeCategories")
  @Override
  public void deleteRecipeCategory(int categoryId) {
    deleteCategory(categoryId);
  }

  @Cacheable(value = "allRecipeCategories")
  @Override
  public List<CategoryResponseDto> getAllRecipeCategories() {
    return getAllCategories();
  }

  @Override
  public CategoryResponseDto getRecipeCategory(int categoryId) {
    return getRecipeCategory(categoryId);
  }
}

package source.code.service.implementation.Plan;

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
import source.code.mapper.Plan.PlanCategoryMapper;
import source.code.model.Plan.PlanCategory;
import source.code.repository.PlanCategoryRepository;
import source.code.service.declaration.PlanCategoryService;
import source.code.service.implementation.Generics.GenericCategoryService;

import java.util.List;

@Service
public class PlanCategoryServiceImpl extends GenericCategoryService<PlanCategory>
        implements PlanCategoryService {
  protected PlanCategoryServiceImpl(ValidationHelper validationHelper,
                                    JsonPatchHelper jsonPatchHelper,
                                    PlanCategoryRepository repository,
                                    PlanCategoryMapper mapper) {
    super(validationHelper, jsonPatchHelper, repository, mapper);
  }

  @CacheEvict(value = "allPlanCategories")
  @Override
  public CategoryResponseDto createPlanCategory(CategoryCreateDto request) {
    return createCategory(request);
  }

  @CacheEvict(value = "allPlanCategories")
  @Override
  public void updatePlanCategory(int categoryId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {
    updateCategory(categoryId, patch);
  }

  @CacheEvict(value = "allPlanCategories")
  @Override
  public void deletePlanCategory(int categoryId) {
    deleteCategory(categoryId);
  }

  @Cacheable(value = "allPlanCategories")
  @Override
  public List<CategoryResponseDto> getAllPlanCategories() {
    return getAllCategories();
  }

  @Override
  public CategoryResponseDto getPlanCategory(int categoryId) {
    return getCategory(categoryId);
  }
}

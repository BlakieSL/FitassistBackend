package source.code.service.implementation.Category;

import org.springframework.stereotype.Service;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Recipe.RecipeCategoryMapper;
import source.code.model.Recipe.RecipeCategory;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.declaration.Category.CategoryService;

@Service("recipeCategoryService")
public class RecipeCategoryServiceImpl
        extends GenericCategoryService<RecipeCategory>
        implements CategoryService {

  protected RecipeCategoryServiceImpl(ValidationServiceImpl validationServiceImpl,
                                      JsonPatchServiceImpl jsonPatchServiceImpl,
                                      RecipeCategoryRepository repository,
                                      RecipeCategoryMapper mapper) {
    super(validationServiceImpl, jsonPatchServiceImpl, repository, mapper);
  }
}

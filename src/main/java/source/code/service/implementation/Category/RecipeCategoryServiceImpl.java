package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.mapper.Recipe.RecipeCategoryMapper;
import source.code.model.Recipe.RecipeCategory;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.declaration.Category.CategoryService;

@Service("recipeCategoryService")
public class RecipeCategoryServiceImpl
        extends GenericCategoryService<RecipeCategory>
        implements CategoryService {

  protected RecipeCategoryServiceImpl(ValidationService validationService,
                                      JsonPatchServiceImpl jsonPatchServiceImpl,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      CacheManager cacheManager,
                                      RecipeCategoryRepository repository,
                                      RecipeCategoryMapper mapper) {
    super(validationService,
            jsonPatchServiceImpl,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

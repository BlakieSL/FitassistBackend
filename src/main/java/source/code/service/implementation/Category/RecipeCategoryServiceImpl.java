package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Category.RecipeCategoryMapper;
import source.code.model.Recipe.RecipeCategory;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.declaration.Category.CategoryService;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;

@Service("recipeCategoryService")
public class RecipeCategoryServiceImpl
        extends GenericCategoryService<RecipeCategory>
        implements CategoryService {

  protected RecipeCategoryServiceImpl(ValidationService validationService,
                                      JsonPatchService jsonPatchService,
                                      CategoryCacheKeyGenerator<RecipeCategory> cacheKeyGenerator,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      CacheManager cacheManager,
                                      RecipeCategoryRepository repository,
                                      RecipeCategoryMapper mapper) {
    super(validationService,
            jsonPatchService,
            cacheKeyGenerator,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

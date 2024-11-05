package source.code.service.implementation.category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.category.RecipeCategoryMapper;
import source.code.model.recipe.RecipeCategory;
import source.code.repository.RecipeCategoryRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.category.CategoryService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;

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

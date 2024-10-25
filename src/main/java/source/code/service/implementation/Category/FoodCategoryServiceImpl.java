package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Category.FoodCategoryMapper;
import source.code.model.Food.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import source.code.service.declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.declaration.Category.CategoryService;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;

@Service("foodCategoryService")
public class FoodCategoryServiceImpl
        extends GenericCategoryService<FoodCategory>
        implements CategoryService {

  protected FoodCategoryServiceImpl(ValidationService validationService,
                                    JsonPatchService jsonPatchService,
                                    CategoryCacheKeyGenerator<FoodCategory> cacheKeyGenerator,
                                    ApplicationEventPublisher applicationEventPublisher,
                                    CacheManager cacheManager,
                                    FoodCategoryRepository repository,
                                    FoodCategoryMapper mapper) {
    super(validationService,
            jsonPatchService,
            cacheKeyGenerator,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

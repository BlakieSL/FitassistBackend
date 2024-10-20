package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Food.FoodCategoryMapper;
import source.code.model.Food.FoodCategory;
import source.code.repository.FoodCategoryRepository;
import source.code.service.declaration.Category.CategoryService;

@Service("foodCategoryService")
public class FoodCategoryServiceImpl
        extends GenericCategoryService<FoodCategory>
        implements CategoryService {

  protected FoodCategoryServiceImpl(ValidationServiceImpl validationServiceImpl,
                                    JsonPatchServiceImpl jsonPatchServiceImpl,
                                    ApplicationEventPublisher applicationEventPublisher,
                                    CacheManager cacheManager,
                                    FoodCategoryRepository repository,
                                    FoodCategoryMapper mapper) {
    super(validationServiceImpl,
            jsonPatchServiceImpl,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

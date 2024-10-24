package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Activity.ActivityCategoryMapper;
import source.code.model.Activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.declaration.Category.CategoryService;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.declaration.Text.TextCacheKeyGenerator;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;

@Service("activityCategoryService")
public class ActivityCategoryServiceImpl
        extends GenericCategoryService<ActivityCategory>
        implements CategoryService {

  protected ActivityCategoryServiceImpl(ValidationService validationService,
                                        JsonPatchService jsonPatchService,
                                        CategoryCacheKeyGenerator<ActivityCategory> cacheKeyGenerator,
                                        ApplicationEventPublisher applicationEventPublisher,
                                        CacheManager cacheManager,
                                        ActivityCategoryRepository repository,
                                        ActivityCategoryMapper mapper) {
    super(validationService,
            jsonPatchService,
            cacheKeyGenerator,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Activity.ActivityCategoryMapper;
import source.code.model.Activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;

import source.code.service.declaration.Category.CategoryService;

@Service("activityCategoryService")
public class ActivityCategoryServiceImpl
        extends GenericCategoryService<ActivityCategory>
        implements CategoryService {

  protected ActivityCategoryServiceImpl(ValidationServiceImpl validationServiceImpl,
                                        JsonPatchServiceImpl jsonPatchServiceImpl,
                                        ApplicationEventPublisher applicationEventPublisher,
                                        CacheManager cacheManager,
                                        ActivityCategoryRepository repository,
                                        ActivityCategoryMapper mapper) {
    super(validationServiceImpl,
            jsonPatchServiceImpl,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

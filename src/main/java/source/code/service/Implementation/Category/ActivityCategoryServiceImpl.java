package source.code.service.Implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Category.ActivityCategoryMapper;
import source.code.model.Activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.Declaration.Category.CategoryService;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;

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

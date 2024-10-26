package source.code.service.Implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.Category.PlanCategoryMapper;
import source.code.model.Plan.PlanCategory;
import source.code.repository.PlanCategoryRepository;
import source.code.service.Declaration.Category.CategoryCacheKeyGenerator;
import source.code.service.Declaration.Category.CategoryService;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;

@Service("planCategoryService")
public class PlanCategoryServiceImpl
        extends GenericCategoryService<PlanCategory>
        implements CategoryService {
  protected PlanCategoryServiceImpl(ValidationService validationService,
                                    JsonPatchService jsonPatchService,
                                    CategoryCacheKeyGenerator<PlanCategory> cacheKeyGenerator,
                                    ApplicationEventPublisher applicationEventPublisher,
                                    CacheManager cacheManager,
                                    PlanCategoryRepository repository,
                                    PlanCategoryMapper mapper) {
    super(validationService,
            jsonPatchService,
            cacheKeyGenerator,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

package source.code.service.implementation.Category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.mapper.Plan.PlanCategoryMapper;
import source.code.model.Plan.PlanCategory;
import source.code.repository.PlanCategoryRepository;
import source.code.service.declaration.Category.CategoryService;

@Service("planCategoryService")
public class PlanCategoryServiceImpl
        extends GenericCategoryService<PlanCategory>
        implements CategoryService {
  protected PlanCategoryServiceImpl(ValidationService validationService,
                                    JsonPatchServiceImpl jsonPatchServiceImpl,
                                    ApplicationEventPublisher applicationEventPublisher,
                                    CacheManager cacheManager,
                                    PlanCategoryRepository repository,
                                    PlanCategoryMapper mapper) {
    super(validationService,
            jsonPatchServiceImpl,
            applicationEventPublisher,
            cacheManager,
            repository,
            mapper);
  }
}

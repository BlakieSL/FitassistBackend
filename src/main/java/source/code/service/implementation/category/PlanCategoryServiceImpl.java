package source.code.service.implementation.category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.category.PlanCategoryMapper;
import source.code.model.plan.PlanCategory;
import source.code.repository.PlanCategoryRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.category.CategoryService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;

@Service("planCategoryService")
public class PlanCategoryServiceImpl extends GenericCategoryService<PlanCategory> implements CategoryService {
    private final PlanCategoryRepository planCategoryRepository;

    protected PlanCategoryServiceImpl(ValidationService validationService,
                                      JsonPatchService jsonPatchService,
                                      CategoryCacheKeyGenerator<PlanCategory> cacheKeyGenerator,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      CacheManager cacheManager,
                                      PlanCategoryRepository planCategoryRepository,
                                      PlanCategoryMapper mapper) {
        super(validationService,
                jsonPatchService,
                cacheKeyGenerator,
                applicationEventPublisher,
                cacheManager,
                planCategoryRepository,
                mapper);
        this.planCategoryRepository = planCategoryRepository;
    }

    @Override
    protected boolean hasAssociatedEntities(int categoryId) {
        return planCategoryRepository.existsByIdAndPlanCategoryAssociationsIsNotEmpty(categoryId);
    }

    @Override
    protected Class<PlanCategory> getEntityClass() {
        return PlanCategory.class;
    }
}

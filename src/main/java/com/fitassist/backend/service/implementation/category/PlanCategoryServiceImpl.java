package com.fitassist.backend.service.implementation.category;

import com.fitassist.backend.mapper.category.PlanCategoryMapper;
import com.fitassist.backend.model.plan.PlanCategory;
import com.fitassist.backend.repository.PlanCategoryRepository;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("planCategoryService")
public class PlanCategoryServiceImpl extends GenericCategoryService<PlanCategory> implements CategoryService {

	private final PlanCategoryRepository planCategoryRepository;

	protected PlanCategoryServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
			CategoryCacheKeyGenerator<PlanCategory> cacheKeyGenerator,
			ApplicationEventPublisher applicationEventPublisher, CacheManager cacheManager,
			PlanCategoryRepository planCategoryRepository, PlanCategoryMapper mapper) {
		super(validationService, jsonPatchService, cacheKeyGenerator, applicationEventPublisher, cacheManager,
				planCategoryRepository, mapper);
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

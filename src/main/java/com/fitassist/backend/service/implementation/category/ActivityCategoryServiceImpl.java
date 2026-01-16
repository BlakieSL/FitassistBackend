package com.fitassist.backend.service.implementation.category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.fitassist.backend.mapper.category.ActivityCategoryMapper;
import com.fitassist.backend.model.activity.ActivityCategory;
import com.fitassist.backend.repository.ActivityCategoryRepository;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;

@Service("activityCategoryService")
public class ActivityCategoryServiceImpl extends GenericCategoryService<ActivityCategory> implements CategoryService {

	private final ActivityCategoryRepository activityCategoryRepository;

	protected ActivityCategoryServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
			CategoryCacheKeyGenerator<ActivityCategory> cacheKeyGenerator,
			ApplicationEventPublisher applicationEventPublisher, CacheManager cacheManager,
			ActivityCategoryRepository activityCategoryRepository, ActivityCategoryMapper mapper) {
		super(validationService, jsonPatchService, cacheKeyGenerator, applicationEventPublisher, cacheManager,
				activityCategoryRepository, mapper);
		this.activityCategoryRepository = activityCategoryRepository;
	}

	@Override
	protected boolean hasAssociatedEntities(int categoryId) {
		return activityCategoryRepository.existsByIdAndActivitiesIsNotEmpty(categoryId);
	}

	@Override
	protected Class<ActivityCategory> getEntityClass() {
		return ActivityCategory.class;
	}

}

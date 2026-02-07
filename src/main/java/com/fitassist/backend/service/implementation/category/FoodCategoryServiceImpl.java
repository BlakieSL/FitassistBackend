package com.fitassist.backend.service.implementation.category;

import com.fitassist.backend.mapper.category.FoodCategoryMapper;
import com.fitassist.backend.model.food.FoodCategory;
import com.fitassist.backend.repository.FoodCategoryRepository;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("foodCategoryService")
public class FoodCategoryServiceImpl extends GenericCategoryService<FoodCategory> implements CategoryService {

	protected FoodCategoryServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
			CategoryCacheKeyGenerator<FoodCategory> cacheKeyGenerator,
			ApplicationEventPublisher applicationEventPublisher, CacheManager cacheManager,
			FoodCategoryRepository foodCategoryRepository, FoodCategoryMapper mapper) {
		super(validationService, jsonPatchService, cacheKeyGenerator, applicationEventPublisher, cacheManager,
				foodCategoryRepository, mapper);
	}

	@Override
	protected Class<FoodCategory> getEntityClass() {
		return FoodCategory.class;
	}

}

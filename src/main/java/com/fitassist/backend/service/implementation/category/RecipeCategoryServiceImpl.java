package com.fitassist.backend.service.implementation.category;

import com.fitassist.backend.mapper.category.RecipeCategoryMapper;
import com.fitassist.backend.model.recipe.RecipeCategory;
import com.fitassist.backend.repository.RecipeCategoryRepository;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("recipeCategoryService")
public class RecipeCategoryServiceImpl extends GenericCategoryService<RecipeCategory> implements CategoryService {

	protected RecipeCategoryServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
			CategoryCacheKeyGenerator<RecipeCategory> cacheKeyGenerator,
			ApplicationEventPublisher applicationEventPublisher, CacheManager cacheManager,
			RecipeCategoryRepository recipeCategoryRepository, RecipeCategoryMapper mapper) {
		super(validationService, jsonPatchService, cacheKeyGenerator, applicationEventPublisher, cacheManager,
				recipeCategoryRepository, mapper);
	}

	@Override
	protected Class<RecipeCategory> getEntityClass() {
		return RecipeCategory.class;
	}

}

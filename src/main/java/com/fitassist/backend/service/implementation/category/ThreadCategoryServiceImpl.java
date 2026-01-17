package com.fitassist.backend.service.implementation.category;

import com.fitassist.backend.mapper.category.ThreadCategoryMapper;
import com.fitassist.backend.model.thread.ThreadCategory;
import com.fitassist.backend.repository.ThreadCategoryRepository;
import com.fitassist.backend.service.declaration.category.CategoryCacheKeyGenerator;
import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("threadCategoryService")
public class ThreadCategoryServiceImpl extends GenericCategoryService<ThreadCategory> implements CategoryService {

	private final ThreadCategoryRepository threadCategoryRepository;

	protected ThreadCategoryServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
			CategoryCacheKeyGenerator<ThreadCategory> cacheKeyGenerator,
			ApplicationEventPublisher applicationEventPublisher, CacheManager cacheManager,
			ThreadCategoryRepository threadCategoryRepository, ThreadCategoryMapper mapper) {
		super(validationService, jsonPatchService, cacheKeyGenerator, applicationEventPublisher, cacheManager,
				threadCategoryRepository, mapper);
		this.threadCategoryRepository = threadCategoryRepository;
	}

	@Override
	protected boolean hasAssociatedEntities(int categoryId) {
		return threadCategoryRepository.existsByIdAndThreadsIsNotEmpty(categoryId);
	}

	@Override
	protected Class<ThreadCategory> getEntityClass() {
		return ThreadCategory.class;
	}

}

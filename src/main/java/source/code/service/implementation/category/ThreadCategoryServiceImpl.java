package source.code.service.implementation.category;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.mapper.category.ThreadCategoryMapper;
import source.code.model.thread.ThreadCategory;
import source.code.repository.ThreadCategoryRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.category.CategoryService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;

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

package source.code.service.implementation.category.cacheKeyGenerator;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.thread.ThreadCategory;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;

@Service
public class ThreadCategoryCacheKeyGeneratorImpl implements CategoryCacheKeyGenerator<ThreadCategory> {
    @Override
    public String generateCacheKey() {
        return CacheKeys.THREAD_CATEGORIES.name();
    }
}

package source.code.service.declaration.text;

public interface TextCacheKeyGenerator<T> {
    String generateCacheKey(T entity);

    String generateCacheKeyForParent(int parentId);
}

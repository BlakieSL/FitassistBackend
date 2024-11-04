package source.code.service.Declaration.Text;

public interface TextCacheKeyGenerator<T> {
    String generateCacheKey(T entity);

    String generateCacheKeyForParent(int parentId);
}

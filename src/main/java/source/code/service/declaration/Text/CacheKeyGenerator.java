package source.code.service.declaration.Text;

public interface CacheKeyGenerator <T>{
  String generateCacheKey(T entity);
  String generateCacheKeyForParent(int parentId);
}

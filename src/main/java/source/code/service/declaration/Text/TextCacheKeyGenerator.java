package source.code.service.declaration.Text;

public interface TextCacheKeyGenerator<T>{
  String generateCacheKey(T entity);
  String generateCacheKeyForParent(int parentId);
}

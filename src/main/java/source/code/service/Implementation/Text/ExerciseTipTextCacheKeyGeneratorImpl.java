package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.model.Text.ExerciseTip;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
public class ExerciseTipTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<ExerciseTip> {
  private static final String CACHE_PREFIX = "exerciseTip_";
  @Override
  public String generateCacheKey(ExerciseTip entity) {
    return CACHE_PREFIX + entity.getExercise().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CACHE_PREFIX + parentId;
  }
}

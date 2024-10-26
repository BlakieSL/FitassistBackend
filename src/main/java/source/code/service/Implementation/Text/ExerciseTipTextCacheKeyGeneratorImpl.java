package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Text.ExerciseTip;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
public class ExerciseTipTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<ExerciseTip> {
  @Override
  public String generateCacheKey(ExerciseTip entity) {
    return CacheKeys.EXERCISE_TIP.toString() + entity.getExercise().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CacheKeys.EXERCISE_TIP.toString() + parentId;
  }
}

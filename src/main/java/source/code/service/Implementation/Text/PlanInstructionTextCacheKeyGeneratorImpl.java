package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Text.PlanInstruction;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
public class PlanInstructionTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<PlanInstruction> {
  @Override
  public String generateCacheKey(PlanInstruction entity) {
    return CacheKeys.PLAN_CATEGORIES.toString() + entity.getPlan().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CacheKeys.PLAN_CATEGORIES.toString() + parentId;
  }
}

package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.model.Text.PlanInstruction;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
public class PlanInstructionTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<PlanInstruction> {
  private static final String CACHE_PREFIX = "planInstruction_";

  @Override
  public String generateCacheKey(PlanInstruction entity) {
    return CACHE_PREFIX + entity.getPlan().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CACHE_PREFIX + parentId;
  }
}

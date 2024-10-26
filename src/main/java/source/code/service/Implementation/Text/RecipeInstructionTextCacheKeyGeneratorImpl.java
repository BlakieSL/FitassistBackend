package source.code.service.Implementation.Text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.CacheKeys;
import source.code.model.Text.RecipeInstruction;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;

@Service
public class RecipeInstructionTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<RecipeInstruction> {

  @Override
  public String generateCacheKey(RecipeInstruction entity) {
    return CacheKeys.RECIPE_INSTRUCTION.toString() + entity.getRecipe().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CacheKeys.RECIPE_INSTRUCTION.toString() + parentId;
  }
}

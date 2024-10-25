package source.code.service.implementation.Text;

import org.springframework.stereotype.Service;
import source.code.model.Text.RecipeInstruction;
import source.code.service.declaration.Text.TextCacheKeyGenerator;

@Service
public class RecipeInstructionTextCacheKeyGeneratorImpl implements TextCacheKeyGenerator<RecipeInstruction> {
  private static final String CACHE_PREFIX = "recipeInstruction_";

  @Override
  public String generateCacheKey(RecipeInstruction entity) {
    return CACHE_PREFIX + entity.getRecipe().getId();
  }

  @Override
  public String generateCacheKeyForParent(int parentId) {
    return CACHE_PREFIX + parentId;
  }
}

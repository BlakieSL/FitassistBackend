package source.code.service.implementation.text;

import org.springframework.stereotype.Service;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.text.RecipeInstruction;
import source.code.service.declaration.text.TextCacheKeyGenerator;

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

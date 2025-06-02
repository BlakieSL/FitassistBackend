package source.code.unit.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.recipe.Recipe;
import source.code.model.text.RecipeInstruction;
import source.code.service.implementation.text.RecipeInstructionTextCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecipeInstructionTextCacheKeyGeneratorTest {
    @InjectMocks
    private RecipeInstructionTextCacheKeyGeneratorImpl keyGenerator;

    @Test
    @DisplayName("generateCacheKey - Should generate correct key for recipe instruction entity")
    public void generateCacheKey() {
        int recipeId = 123;
        RecipeInstruction recipeInstruction = new RecipeInstruction();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipeInstruction.setRecipe(recipe);

        String result = keyGenerator.generateCacheKey(recipeInstruction);

        String expected = CacheKeys.RECIPE_INSTRUCTION.toString() + recipeId;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("generateCacheKeyForParent - Should generate correct key for recipe ID")
    public void generateCacheKeyForParent() {
        int recipeId = 123;

        String result = keyGenerator.generateCacheKeyForParent(recipeId);

        String expected = CacheKeys.RECIPE_INSTRUCTION.toString() + recipeId;
        assertEquals(expected, result);
    }
}
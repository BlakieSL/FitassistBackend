package unit.category.cacheKeyGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.category.cacheKeyGenerator.RecipeCategoryCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecipeCategoryCacheKeyGeneratorTest {
    @InjectMocks
    private RecipeCategoryCacheKeyGeneratorImpl recipeCategoryCacheKeyGenerator;

    @Test
    void generateCacheKey_shouldReturn() {
        String expectedCacheKey = "RECIPE_CATEGORIES";

        String actualCacheKey = recipeCategoryCacheKeyGenerator.generateCacheKey();

        assertEquals(expectedCacheKey, actualCacheKey);
    }
}

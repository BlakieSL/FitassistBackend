package source.code.service.category.cacheKeyGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.category.cacheKeyGenerator.PlanCategoryCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PlanCategoryCacheKeyGeneratorTest {
    @InjectMocks
    private PlanCategoryCacheKeyGeneratorImpl planCategoryCacheKeyGenerator;

    @Test
    void generateCacheKey_shouldReturn() {
        String expectedCacheKey = "PLAN_CATEGORIES";

        String actualCacheKey = planCategoryCacheKeyGenerator.generateCacheKey();

        assertEquals(expectedCacheKey, actualCacheKey);
    }
}

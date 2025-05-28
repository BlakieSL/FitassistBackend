package unit.category.cacheKeyGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.category.cacheKeyGenerator.TargetMuscleCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TargetMuscleCacheKeyGeneratorTest {
    @InjectMocks
    private TargetMuscleCacheKeyGeneratorImpl exerciseCategoryCacheKeyGenerator;

    @Test
    void generateCacheKey_shouldReturn() {
        String expectedCacheKey = "TARGET_MUSCLE";

        String actualCacheKey = exerciseCategoryCacheKeyGenerator.generateCacheKey();

        assertEquals(expectedCacheKey, actualCacheKey);
    }
}

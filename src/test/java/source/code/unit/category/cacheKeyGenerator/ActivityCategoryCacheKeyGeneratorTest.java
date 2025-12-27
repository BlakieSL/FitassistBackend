package source.code.unit.category.cacheKeyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.category.cacheKeyGenerator.ActivityCategoryCacheKeyGeneratorImpl;

@ExtendWith(MockitoExtension.class)
public class ActivityCategoryCacheKeyGeneratorTest {

	@InjectMocks
	private ActivityCategoryCacheKeyGeneratorImpl cacheKeyGenerator;

	@Test
	void shouldReturnCorrectCacheKey() {
		String expectedCacheKey = "ACTIVITY_CATEGORIES";

		String actualCacheKey = cacheKeyGenerator.generateCacheKey();

		assertEquals(expectedCacheKey, actualCacheKey);
	}

}

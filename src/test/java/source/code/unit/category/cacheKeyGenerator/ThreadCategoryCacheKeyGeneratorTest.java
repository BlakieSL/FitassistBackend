package source.code.unit.category.cacheKeyGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.category.cacheKeyGenerator.ThreadCategoryCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ThreadCategoryCacheKeyGeneratorTest {

	@InjectMocks
	private ThreadCategoryCacheKeyGeneratorImpl threadCategoryCacheKeyGenerator;

	@Test
	void generateCacheKey_shouldReturn() {
		String expectedCacheKey = "THREAD_CATEGORIES";

		String actualCacheKey = threadCategoryCacheKeyGenerator.generateCacheKey();

		assertEquals(expectedCacheKey, actualCacheKey);
	}

}

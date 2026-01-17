package com.fitassist.backend.unit.category.cacheKeyGenerator;

import com.fitassist.backend.service.implementation.category.cacheKeyGenerator.ThreadCategoryCacheKeyGeneratorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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

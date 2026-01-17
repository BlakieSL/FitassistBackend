package com.fitassist.backend.unit.category.cacheKeyGenerator;

import com.fitassist.backend.service.implementation.category.cacheKeyGenerator.ActivityCategoryCacheKeyGeneratorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

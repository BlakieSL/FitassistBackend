package com.fitassist.backend.unit.category.cacheKeyGenerator;

import com.fitassist.backend.service.implementation.category.cacheKeyGenerator.PlanCategoryCacheKeyGeneratorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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

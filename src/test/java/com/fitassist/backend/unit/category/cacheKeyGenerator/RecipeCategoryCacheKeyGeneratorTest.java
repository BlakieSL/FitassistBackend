package com.fitassist.backend.unit.category.cacheKeyGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fitassist.backend.service.implementation.category.cacheKeyGenerator.RecipeCategoryCacheKeyGeneratorImpl;

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

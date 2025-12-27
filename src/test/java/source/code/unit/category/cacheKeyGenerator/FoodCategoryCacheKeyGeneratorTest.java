package source.code.unit.category.cacheKeyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.category.cacheKeyGenerator.FoodCategoryCacheKeyGeneratorImpl;

@ExtendWith(MockitoExtension.class)
public class FoodCategoryCacheKeyGeneratorTest {

	@InjectMocks
	private FoodCategoryCacheKeyGeneratorImpl foodCategoryCacheKeyGenerator;

	@Test
	void generateCacheKey_shouldReturn() {
		String expectedCacheKey = "FOOD_CATEGORIES";

		String actualCacheKey = foodCategoryCacheKeyGenerator.generateCacheKey();

		assertEquals(expectedCacheKey, actualCacheKey);
	}

}

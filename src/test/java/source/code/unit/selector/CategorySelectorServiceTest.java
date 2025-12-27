package source.code.unit.selector;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.model.CategoryType;
import source.code.service.implementation.category.ActivityCategoryServiceImpl;
import source.code.service.implementation.category.FoodCategoryServiceImpl;
import source.code.service.implementation.category.PlanCategoryServiceImpl;
import source.code.service.implementation.category.RecipeCategoryServiceImpl;
import source.code.service.implementation.category.ThreadCategoryServiceImpl;
import source.code.service.implementation.selector.CategorySelectorServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategorySelectorServiceTest {

	@Mock
	private FoodCategoryServiceImpl foodCategoryService;

	@Mock
	private ActivityCategoryServiceImpl activityCategoryService;

	@Mock
	private RecipeCategoryServiceImpl recipeCategoryService;

	@Mock
	private PlanCategoryServiceImpl planCategoryService;

	@Mock
	private ThreadCategoryServiceImpl threadCategoryService;

	private CategorySelectorServiceImpl categorySelectorService;

	@BeforeEach
	void setUp() {
		categorySelectorService = new CategorySelectorServiceImpl(foodCategoryService, activityCategoryService,
			recipeCategoryService, planCategoryService, threadCategoryService);
	}

	@Test
	void getService_shouldReturnFoodCategoryService() {
		assertSame(foodCategoryService, categorySelectorService.getService(CategoryType.FOOD));
	}

	@Test
	void getService_shouldReturnActivityCategoryService() {
		assertSame(activityCategoryService, categorySelectorService.getService(CategoryType.ACTIVITY));
	}

	@Test
	void getService_shouldReturnRecipeCategoryService() {
		assertSame(recipeCategoryService, categorySelectorService.getService(CategoryType.RECIPE));
	}

	@Test
	void getService_shouldReturnPlanCategoryService() {
		assertSame(planCategoryService, categorySelectorService.getService(CategoryType.PLAN));
	}

	@Test
	void getService_shouldReturnThreadCategoryService() {
		assertSame(threadCategoryService, categorySelectorService.getService(CategoryType.FORUM_THREAD));
	}

}

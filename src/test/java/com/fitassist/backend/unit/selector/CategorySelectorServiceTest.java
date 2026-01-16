package com.fitassist.backend.unit.selector;

import com.fitassist.backend.service.implementation.category.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fitassist.backend.service.implementation.selector.CategoryType;
import source.code.service.implementation.category.*;
import com.fitassist.backend.service.implementation.selector.CategorySelectorServiceImpl;

import static org.junit.jupiter.api.Assertions.assertSame;

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

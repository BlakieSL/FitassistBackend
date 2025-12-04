package source.code.unit.selector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.model.CategoryType;
import source.code.service.implementation.category.*;
import source.code.service.implementation.selector.CategorySelectorServiceImpl;

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

    private CategorySelectorServiceImpl categorySelectorService;

    @BeforeEach
    void setUp() {
        categorySelectorService = new CategorySelectorServiceImpl(
                foodCategoryService,
                activityCategoryService,
                recipeCategoryService,
                planCategoryService
        );
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
}

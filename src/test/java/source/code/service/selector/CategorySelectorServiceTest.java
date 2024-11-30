package source.code.service.selector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import source.code.helper.Enum.model.CategoryType;
import source.code.service.declaration.category.CategoryService;
import source.code.service.implementation.category.*;
import source.code.service.implementation.selector.CategorySelectorServiceImpl;

import static org.junit.jupiter.api.Assertions.assertSame;

public class CategorySelectorServiceTest {

    @Mock
    private FoodCategoryServiceImpl foodCategoryService;
    @Mock
    private ActivityCategoryServiceImpl activityCategoryService;
    @Mock
    private TargetMuscleServiceImpl targetMuscleService;
    @Mock
    private RecipeCategoryServiceImpl recipeCategoryService;
    @Mock
    private PlanCategoryServiceImpl planCategoryService;

    private CategorySelectorServiceImpl categorySelectorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categorySelectorService = new CategorySelectorServiceImpl(
                foodCategoryService,
                activityCategoryService,
                targetMuscleService,
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
    void getService_shouldReturnTargetMuscleService() {
        assertSame(targetMuscleService, categorySelectorService.getService(CategoryType.EXERCISE));
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
package source.code.service.implementation.Selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.enumerators.CategoryType;
import source.code.service.declaration.Category.CategoryService;
import source.code.service.declaration.Selector.CategorySelectorService;

@Service
public class CategorySelectorServiceImpl implements CategorySelectorService {
  private final CategoryService foodCategoryService;
  private final CategoryService activityCategoryService;
  private final CategoryService exerciseCategoryService;
  private final CategoryService recipeCategoryService;
  private final CategoryService planCategoryService;

  public CategorySelectorServiceImpl(@Qualifier("foodCategoryService") CategoryService foodCategoryService,
                                     @Qualifier("activityCategoryService") CategoryService activityCategoryService,
                                     @Qualifier("exerciseCategoryService") CategoryService exerciseCategoryService,
                                     @Qualifier("recipeCategoryService") CategoryService recipeCategoryService,
                                     @Qualifier("planCategoryService") CategoryService planCategoryService) {
    this.foodCategoryService = foodCategoryService;
    this.activityCategoryService = activityCategoryService;
    this.exerciseCategoryService = exerciseCategoryService;
    this.recipeCategoryService = recipeCategoryService;
    this.planCategoryService = planCategoryService;
  }

  @Override
  public CategoryService getService(CategoryType categoryType) {
    return switch (categoryType) {
      case FOOD -> foodCategoryService;
      case ACTIVITY -> activityCategoryService;
      case EXERCISE -> exerciseCategoryService;
      case RECIPE -> recipeCategoryService;
      case PLAN -> planCategoryService;
    };
  }
}

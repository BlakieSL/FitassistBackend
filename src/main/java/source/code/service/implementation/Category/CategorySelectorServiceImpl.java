package source.code.service.implementation.Category;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.enumerators.CategoryType;
import source.code.service.declaration.Category.CategorySelectorService;
import source.code.service.declaration.Category.CategoryService;

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
    switch (categoryType) {
      case FOOD:
        return foodCategoryService;
      case ACTIVITY:
        return activityCategoryService;
      case EXERCISE:
        return exerciseCategoryService;
      case RECIPE:
        return recipeCategoryService;
      case PLAN:
        return planCategoryService;
      default:
        throw new IllegalArgumentException("Invalid category type: " + categoryType);
    }
  }
}

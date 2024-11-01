package source.code.service.Implementation.Selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.Enum.CategoryType;
import source.code.service.Declaration.Category.CategoryService;
import source.code.service.Declaration.Selector.CategorySelectorService;

@Service
public class CategorySelectorServiceImpl implements CategorySelectorService {
  private final CategoryService foodCategoryService;
  private final CategoryService activityCategoryService;
  private final CategoryService targetMuscleService;
  private final CategoryService recipeCategoryService;
  private final CategoryService planCategoryService;

  public CategorySelectorServiceImpl(@Qualifier("foodCategoryService") CategoryService foodCategoryService,
                                     @Qualifier("activityCategoryService") CategoryService activityCategoryService,
                                     @Qualifier("targetMuscleService") CategoryService targetMuscleService,
                                     @Qualifier("recipeCategoryService") CategoryService recipeCategoryService,
                                     @Qualifier("planCategoryService") CategoryService planCategoryService) {
    this.foodCategoryService = foodCategoryService;
    this.activityCategoryService = activityCategoryService;
    this.targetMuscleService = targetMuscleService;
    this.recipeCategoryService = recipeCategoryService;
    this.planCategoryService = planCategoryService;
  }

  @Override
  public CategoryService getService(CategoryType categoryType) {
    return switch (categoryType) {
      case FOOD -> foodCategoryService;
      case ACTIVITY -> activityCategoryService;
      case EXERCISE -> targetMuscleService;
      case RECIPE -> recipeCategoryService;
      case PLAN -> planCategoryService;
    };
  }
}

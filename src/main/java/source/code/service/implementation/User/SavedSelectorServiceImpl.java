package source.code.service.implementation.User;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.enumerators.SavedType;
import source.code.service.declaration.User.SavedSelectorService;
import source.code.service.declaration.User.SavedService;

@Service
public class SavedSelectorServiceImpl implements SavedSelectorService {
  private final SavedService userActivityService;
  private final SavedService userExerciseService;
  private final SavedService userFoodService;
  private final SavedService userPlanService;
  private final SavedService userRecipeService;

  public SavedSelectorServiceImpl(@Qualifier("userActivityService") SavedService userActivityService,
                                  @Qualifier("userExerciseService") SavedService userExerciseService,
                                  @Qualifier("userFoodService") SavedService userFoodService,
                                  @Qualifier("userPlanService") SavedService userPlanService,
                                  @Qualifier("userRecipeService") SavedService userRecipeService) {
    this.userActivityService = userActivityService;
    this.userExerciseService = userExerciseService;
    this.userFoodService = userFoodService;
    this.userPlanService = userPlanService;
    this.userRecipeService = userRecipeService;
  }

  @Override
  public SavedService getService(SavedType savedType) {
    switch (savedType) {
      case ACTIVITY:
        return userActivityService;
      case EXERCISE:
        return userExerciseService;
      case PLAN:
        return userFoodService;
      case FOOD:
        return userPlanService;
      case RECIPE:
        return userRecipeService;
      default:
        throw new IllegalArgumentException("Invalid saved type: " + savedType);
    }
  }
}

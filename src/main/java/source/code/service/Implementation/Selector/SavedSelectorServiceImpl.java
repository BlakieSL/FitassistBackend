package source.code.service.Implementation.Selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.Enum.EntityType;
import source.code.service.Declaration.Selector.SavedSelectorService;
import source.code.service.Declaration.User.SavedService;

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
  public SavedService getService(EntityType entityType) {
    return switch (entityType) {
      case ACTIVITY -> userActivityService;
      case EXERCISE -> userExerciseService;
      case PLAN -> userFoodService;
      case FOOD -> userPlanService;
      case RECIPE -> userRecipeService;
    };
  }
}

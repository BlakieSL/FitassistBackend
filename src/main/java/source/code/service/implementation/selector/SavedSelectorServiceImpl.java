package source.code.service.implementation.selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.Enum.model.SavedEntityType;
import source.code.service.declaration.selector.SavedSelectorService;
import source.code.service.declaration.user.SavedService;
import source.code.service.declaration.user.SavedServiceWithoutType;

@Service
public class SavedSelectorServiceImpl implements SavedSelectorService {
    private final SavedService userActivityService;
    private final SavedService userExerciseService;
    private final SavedService userFoodService;
    private final SavedService userPlanService;
    private final SavedService userRecipeService;
    private final SavedServiceWithoutType userCommentService;
    private final SavedServiceWithoutType userThreadService;
    public SavedSelectorServiceImpl(@Qualifier("userActivityService")
                                    SavedService userActivityService,
                                    @Qualifier("userExerciseService")
                                    SavedService userExerciseService,
                                    @Qualifier("userFoodService")
                                    SavedService userFoodService,
                                    @Qualifier("userPlanService")
                                    SavedService userPlanService,
                                    @Qualifier("userRecipeService")
                                    SavedService userRecipeService,
                                    @Qualifier("userCommentService")
                                    SavedServiceWithoutType userCommentService,
                                    @Qualifier("userThreadService")
                                    SavedServiceWithoutType userThreadService) {
        this.userActivityService = userActivityService;
        this.userExerciseService = userExerciseService;
        this.userFoodService = userFoodService;
        this.userPlanService = userPlanService;
        this.userRecipeService = userRecipeService;
        this.userCommentService = userCommentService;
        this.userThreadService = userThreadService;
    }

    @Override
    public SavedService getService(SavedEntityType savedEntityType) {
        return switch (savedEntityType) {
            case ACTIVITY -> userActivityService;
            case EXERCISE -> userExerciseService;
            case PLAN -> userFoodService;
            case FOOD -> userPlanService;
            case RECIPE -> userRecipeService;
            default -> throw new IllegalStateException("Unexpected value: " + savedEntityType);
        };
    }

    @Override
    public SavedServiceWithoutType getServiceWithoutType(SavedEntityType savedEntityType) {
        return switch (savedEntityType) {
            case COMMENT -> userCommentService;
            case FORUM_THREAD -> userThreadService;
            default -> throw new IllegalStateException("Unexpected value: " + savedEntityType);
        };
    }
}

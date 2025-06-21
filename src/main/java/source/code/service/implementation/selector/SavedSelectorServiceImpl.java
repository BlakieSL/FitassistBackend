package source.code.service.implementation.selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.Enum.model.SavedEntityType;
import source.code.service.declaration.selector.SavedSelectorService;
import source.code.service.declaration.user.SavedService;
import source.code.service.declaration.user.SavedServiceWithoutType;

@Service
public class SavedSelectorServiceImpl implements SavedSelectorService {
    private final SavedService userPlanService;
    private final SavedService userRecipeService;
    private final SavedService userCommentService;
    private final SavedServiceWithoutType userExerciseService;
    private final SavedServiceWithoutType userFoodService;
    private final SavedServiceWithoutType userActivityService;
    private final SavedServiceWithoutType userThreadService;
    public SavedSelectorServiceImpl(@Qualifier("userPlanService")
                                    SavedService userPlanService,
                                    @Qualifier("userRecipeService")
                                    SavedService userRecipeService,
                                    @Qualifier("userCommentService")
                                    SavedService userCommentService,
                                    @Qualifier("userExerciseService")
                                    SavedServiceWithoutType userExerciseService,
                                    @Qualifier("userFoodService")
                                    SavedServiceWithoutType userFoodService,
                                    @Qualifier("userActivityService")
                                    SavedServiceWithoutType userActivityService,
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
            case PLAN -> userPlanService;
            case RECIPE -> userRecipeService;
            case COMMENT -> userCommentService;
            default -> throw new IllegalStateException("Unexpected value: " + savedEntityType);
        };
    }

    @Override
    public SavedServiceWithoutType getServiceWithoutType(SavedEntityType savedEntityType) {
        return switch (savedEntityType) {
            case FORUM_THREAD -> userThreadService;
            case ACTIVITY -> userActivityService;
            case FOOD -> userFoodService;
            case EXERCISE -> userExerciseService;
            default -> throw new IllegalStateException("Unexpected value: " + savedEntityType);
        };
    }
}

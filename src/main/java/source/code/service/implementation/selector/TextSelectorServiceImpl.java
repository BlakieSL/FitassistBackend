package source.code.service.implementation.selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.Enum.TextType;
import source.code.service.declaration.selector.TextSelectorService;
import source.code.service.declaration.text.TextService;

@Service
public class TextSelectorServiceImpl implements TextSelectorService {
    private final TextService exerciseInstructionService;
    private final TextService exerciseTipService;
    private final TextService recipeInstructionService;
    private final TextService planInstructionService;

    public TextSelectorServiceImpl(@Qualifier("exerciseInstructionService")
                                   TextService exerciseInstructionService,
                                   @Qualifier("exerciseTipService")
                                   TextService exerciseTipService,
                                   @Qualifier("recipeInstructionService")
                                   TextService recipeInstructionService,
                                   @Qualifier("planInstructionService")
                                   TextService planInstructionService) {
        this.exerciseInstructionService = exerciseInstructionService;
        this.exerciseTipService = exerciseTipService;
        this.recipeInstructionService = recipeInstructionService;
        this.planInstructionService = planInstructionService;
    }

    @Override
    public TextService getService(TextType textType) {
        return switch (textType) {
            case EXERCISE_INSTRUCTION -> exerciseInstructionService;
            case EXERCISE_TIP -> exerciseTipService;
            case RECIPE_INSTRUCTION -> recipeInstructionService;
            case PLAN_INSTRUCTION -> planInstructionService;
        };
    }
}

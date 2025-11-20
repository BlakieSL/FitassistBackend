package source.code.unit.selector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import source.code.helper.Enum.model.TextType;
import source.code.service.declaration.text.TextService;
import source.code.service.implementation.selector.TextSelectorServiceImpl;

import static org.junit.jupiter.api.Assertions.assertSame;

public class TextSelectorServiceTest {

    @Mock
    private TextService exerciseInstructionService;
    @Mock
    private TextService exerciseTipService;
    @Mock
    private TextService recipeInstructionService;
    @Mock
    private TextService planInstructionService;

    private TextSelectorServiceImpl textSelectorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        textSelectorService = new TextSelectorServiceImpl(
                exerciseInstructionService,
                exerciseTipService,
                recipeInstructionService,
                planInstructionService
        );
    }

    @Test
    void getService_shouldReturnExerciseInstructionService() {
        assertSame(
                exerciseInstructionService,
                textSelectorService.getService(TextType.EXERCISE_INSTRUCTION)
        );
    }

    @Test
    void getService_shouldReturnExerciseTipService() {
        assertSame(exerciseTipService, textSelectorService.getService(TextType.EXERCISE_TIP));
    }

    @Test
    void getService_shouldReturnRecipeInstructionService() {
        assertSame(
                recipeInstructionService,
                textSelectorService.getService(TextType.RECIPE_INSTRUCTION)
        );
    }

    @Test
    void getService_shouldReturnPlanInstructionService() {
        assertSame(planInstructionService, textSelectorService.getService(TextType.PLAN_INSTRUCTION));
    }
}

package source.code.unit.text;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.plan.Plan;
import source.code.model.text.PlanInstruction;
import source.code.service.implementation.text.PlanInstructionTextCacheKeyGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PlanInstructionTextCacheKeyGeneratorTest {
    @InjectMocks
    private PlanInstructionTextCacheKeyGeneratorImpl keyGenerator;

    @Test
    @DisplayName("generateCacheKey - Should generate correct key for plan instruction entity")
    public void generateCacheKey() {
        int planId = 123;
        PlanInstruction planInstruction = new PlanInstruction();
        Plan plan = new Plan();
        plan.setId(planId);
        planInstruction.setPlan(plan);

        String result = keyGenerator.generateCacheKey(planInstruction);

        String expected = CacheKeys.PLAN_INSTRUCTION.toString() + planId;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("generateCacheKeyForParent - Should generate correct key for plan ID")
    public void generateCacheKeyForParent() {
        int planId = 123;

        String result = keyGenerator.generateCacheKeyForParent(planId);

        String expected = CacheKeys.PLAN_INSTRUCTION.toString() + planId;
        assertEquals(expected, result);
    }
}

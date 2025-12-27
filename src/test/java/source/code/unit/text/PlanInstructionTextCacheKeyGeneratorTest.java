package source.code.unit.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.Enum.cache.CacheKeys;
import source.code.model.plan.Plan;
import source.code.model.text.PlanInstruction;
import source.code.service.implementation.text.PlanInstructionTextCacheKeyGeneratorImpl;

@ExtendWith(MockitoExtension.class)
public class PlanInstructionTextCacheKeyGeneratorTest {

	@InjectMocks
	private PlanInstructionTextCacheKeyGeneratorImpl keyGenerator;

	@Test
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
	public void generateCacheKeyForParent() {
		int planId = 123;

		String result = keyGenerator.generateCacheKeyForParent(planId);

		String expected = CacheKeys.PLAN_INSTRUCTION.toString() + planId;
		assertEquals(expected, result);
	}

}

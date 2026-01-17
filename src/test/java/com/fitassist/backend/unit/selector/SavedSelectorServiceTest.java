package com.fitassist.backend.unit.selector;

import com.fitassist.backend.service.declaration.user.SavedService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import com.fitassist.backend.service.implementation.selector.SavedEntityType;
import com.fitassist.backend.service.implementation.selector.SavedSelectorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SavedSelectorServiceTest {

	@Mock
	private SavedService userCommentService;

	@Mock
	private SavedService userPlanService;

	@Mock
	private SavedService userRecipeService;

	@Mock
	private SavedServiceWithoutType userActivityService;

	@Mock
	private SavedServiceWithoutType userExerciseService;

	@Mock
	private SavedServiceWithoutType userFoodService;

	@Mock
	private SavedServiceWithoutType userThreadService;

	private SavedSelectorServiceImpl savedSelectorService;

	@BeforeEach
	void setUp() {
		savedSelectorService = new SavedSelectorServiceImpl(userPlanService, userRecipeService, userCommentService,
				userExerciseService, userFoodService, userActivityService, userThreadService);
	}

	@Test
	void getService_shouldReturnUserPlanService() {
		assertSame(userPlanService, savedSelectorService.getService(SavedEntityType.PLAN));
	}

	@Test
	void getService_shouldReturnUserRecipeService() {
		assertSame(userRecipeService, savedSelectorService.getService(SavedEntityType.RECIPE));
	}

	@Test
	void getService_shouldReturnUserCommentService() {
		assertSame(userCommentService, savedSelectorService.getService(SavedEntityType.COMMENT));
	}

	@Test
	void getService_shouldThrowExceptionForUnexpectedValue() {
		assertThrows(IllegalStateException.class, () -> savedSelectorService.getService(SavedEntityType.FOOD));
	}

	@Test
	void getServiceWithoutType_shouldReturnUserThreadService() {
		assertSame(userThreadService, savedSelectorService.getServiceWithoutType(SavedEntityType.FORUM_THREAD));
	}

	@Test
	void getServiceWithoutType_shouldReturnUserActivityService() {
		assertSame(userActivityService, savedSelectorService.getServiceWithoutType(SavedEntityType.ACTIVITY));
	}

	@Test
	void getServiceWithoutType_shouldReturnUserFoodService() {
		assertSame(userFoodService, savedSelectorService.getServiceWithoutType(SavedEntityType.FOOD));
	}

	@Test
	void getServiceWithoutType_shouldReturnUserExerciseService() {
		assertSame(userExerciseService, savedSelectorService.getServiceWithoutType(SavedEntityType.EXERCISE));
	}

	@Test
	void getServiceWithoutType_shouldThrowExceptionForUnexpectedValue() {
		assertThrows(IllegalStateException.class,
				() -> savedSelectorService.getServiceWithoutType(SavedEntityType.PLAN));
	}

}

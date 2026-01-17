package com.fitassist.backend.service.implementation.selector;

import com.fitassist.backend.service.declaration.selector.SavedSelectorService;
import com.fitassist.backend.service.declaration.user.SavedService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SavedSelectorServiceImpl implements SavedSelectorService {

	private final SavedService userPlanService;

	private final SavedService userRecipeService;

	private final SavedService userCommentService;

	private final SavedServiceWithoutType userExerciseService;

	private final SavedServiceWithoutType userFoodService;

	private final SavedServiceWithoutType userActivityService;

	private final SavedServiceWithoutType userThreadService;

	public SavedSelectorServiceImpl(@Qualifier("userPlanService") SavedService userPlanService,
			@Qualifier("userRecipeService") SavedService userRecipeService,
			@Qualifier("userCommentService") SavedService userCommentService,
			@Qualifier("userExerciseService") SavedServiceWithoutType userExerciseService,
			@Qualifier("userFoodService") SavedServiceWithoutType userFoodService,
			@Qualifier("userActivityService") SavedServiceWithoutType userActivityService,
			@Qualifier("userThreadService") SavedServiceWithoutType userThreadService) {
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

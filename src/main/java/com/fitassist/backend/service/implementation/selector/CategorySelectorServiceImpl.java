package com.fitassist.backend.service.implementation.selector;

import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.declaration.selector.CategorySelectorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CategorySelectorServiceImpl implements CategorySelectorService {

	private final CategoryService foodCategoryService;

	private final CategoryService activityCategoryService;

	private final CategoryService recipeCategoryService;

	private final CategoryService planCategoryService;

	private final CategoryService threadCategoryService;

	public CategorySelectorServiceImpl(@Qualifier("foodCategoryService") CategoryService foodCategoryService,
			@Qualifier("activityCategoryService") CategoryService activityCategoryService,
			@Qualifier("recipeCategoryService") CategoryService recipeCategoryService,
			@Qualifier("planCategoryService") CategoryService planCategoryService,
			@Qualifier("threadCategoryService") CategoryService threadCategoryService) {
		this.foodCategoryService = foodCategoryService;
		this.activityCategoryService = activityCategoryService;
		this.recipeCategoryService = recipeCategoryService;
		this.planCategoryService = planCategoryService;
		this.threadCategoryService = threadCategoryService;
	}

	@Override
	public CategoryService getService(CategoryType categoryType) {
		return switch (categoryType) {
			case FOOD -> foodCategoryService;
			case ACTIVITY -> activityCategoryService;
			case RECIPE -> recipeCategoryService;
			case PLAN -> planCategoryService;
			case FORUM_THREAD -> threadCategoryService;
		};
	}

}

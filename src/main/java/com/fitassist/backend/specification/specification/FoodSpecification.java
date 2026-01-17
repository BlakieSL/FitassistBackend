package com.fitassist.backend.specification.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.PredicateContext;
import com.fitassist.backend.specification.specification.field.FoodField;
import com.fitassist.backend.specification.specification.field.LikesAndSaves;
import jakarta.persistence.criteria.Predicate;

public class FoodSpecification extends AbstractSpecification<Food, FoodField> {

	private static final String FOOD_CATEGORY_FIELD = "foodCategory";

	private static final String CALORIES_FIELD = "calories";

	private static final String PROTEIN_FIELD = "protein";

	private static final String FAT_FIELD = "fat";

	private static final String CARBOHYDRATES_FIELD = "carbohydrates";

	public FoodSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		super(criteria, dependencies);
	}

	@Override
	protected Class<FoodField> getFieldClass() {
		return FoodField.class;
	}

	@Override
	protected Predicate buildPredicateForField(PredicateContext<Food> context, FoodField field) {
		return switch (field) {
			case CALORIES ->
				GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(CALORIES_FIELD));
			case PROTEIN ->
				GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(PROTEIN_FIELD));
			case FAT ->
				GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(FAT_FIELD));
			case CARBOHYDRATES -> GenericSpecificationHelper.buildPredicateNumericProperty(context,
					context.root().get(CARBOHYDRATES_FIELD));
			case CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(context, FOOD_CATEGORY_FIELD);
			case SAVED_BY_USER ->
				GenericSpecificationHelper.buildSavedByUserPredicate(context, LikesAndSaves.USER_FOODS.getFieldName());
			case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
					LikesAndSaves.USER_FOODS.getFieldName(), null, null);
		};
	}

}

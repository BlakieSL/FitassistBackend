package com.fitassist.backend.specification.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.PredicateContext;
import com.fitassist.backend.specification.specification.field.ActivityField;
import com.fitassist.backend.specification.specification.field.LikesAndSaves;
import jakarta.persistence.criteria.Predicate;

public class ActivitySpecification extends AbstractSpecification<Activity, ActivityField> {

	private static final String ACTIVITY_CATEGORY_FIELD = "activityCategory";

	private static final String MET_FIELD = "met";

	public ActivitySpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		super(criteria, dependencies);
	}

	@Override
	protected Class<ActivityField> getFieldClass() {
		return ActivityField.class;
	}

	@Override
	protected Predicate buildPredicateForField(PredicateContext<Activity> context, ActivityField field) {
		return switch (field) {
			case CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(context, ACTIVITY_CATEGORY_FIELD);
			case MET ->
				GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(MET_FIELD));
			case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_ACTIVITIES.getFieldName());
			case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
					LikesAndSaves.USER_ACTIVITIES.getFieldName(), null, null);
		};
	}

}

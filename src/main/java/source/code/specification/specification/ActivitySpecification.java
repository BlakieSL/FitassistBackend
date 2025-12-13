package source.code.specification.specification;

import jakarta.persistence.criteria.Predicate;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ActivityField;
import source.code.model.activity.Activity;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

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
            case MET -> GenericSpecificationHelper.buildPredicateNumericProperty(context, context.root().get(MET_FIELD));
            case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    context, LikesAndSaves.USER_ACTIVITIES.getFieldName());
            case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    context, LikesAndSaves.USER_ACTIVITIES.getFieldName(), null, null);
        };
    }
}

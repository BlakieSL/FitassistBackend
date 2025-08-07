package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ActivityField;
import source.code.helper.Enum.model.field.ExerciseField;
import source.code.model.activity.Activity;
import source.code.model.exercise.Exercise;
import source.code.model.plan.Plan;
import source.code.model.plan.PlanCategoryAssociation;

public class ActivitySpecification implements Specification<Activity> {
    private static final String ACTIVITY_CATEGORY_FIELD = "activityCategory";
    private static final String MET_FIELD = "met";

    private final FilterCriteria criteria;

    public ActivitySpecification(@NonNull FilterCriteria criteria) {
        this.criteria = criteria;
    }

    public static ActivitySpecification of(@NonNull FilterCriteria criteria) {
        return new ActivitySpecification(criteria);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Activity> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        initializeFetches(root);
        return buildPredicateForField(builder, criteria, root, resolveActivityField(criteria), query);
    }

    private void initializeFetches(Root<Activity> root) {
        root.fetch(ACTIVITY_CATEGORY_FIELD, JoinType.LEFT);
    }

    private ActivityField resolveActivityField(FilterCriteria criteria) {
        try {
            return ActivityField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }
    }

    private Predicate buildPredicateForField(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<Activity> root,
            ActivityField field,
            CriteriaQuery<?> query) {
        return switch (field) {
            case ActivityField.CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    ActivityField.CATEGORY.getFieldName()
            );
            case ActivityField.MET -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder,
                    criteria,
                    root.get(ActivityField.MET.getFieldName())
            );
            case ActivityField.SAVE -> {
                Predicate predicate = GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                        builder,
                        criteria,
                        root,
                        LikesAndSaves.USER_ACTIVITIES.getFieldName(),
                        null,
                        null
                );

                query.groupBy(root.get("id"));

                yield  predicate;
            }
        };
    }
}

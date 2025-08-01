package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ActivityField;
import source.code.model.activity.Activity;

public class ActivitySpecification implements Specification<Activity> {
    private final FilterCriteria criteria;

    public ActivitySpecification(FilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Activity> root, CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        root.fetch("activityCategory", JoinType.LEFT);
        ActivityField field;

        try {
            field = ActivityField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }

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

    public static ActivitySpecification of(FilterCriteria criteria) {
        return new ActivitySpecification(criteria);
    }
}

package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ActivityField;
import source.code.model.activity.Activity;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

@AllArgsConstructor(staticName = "of")
public class ActivitySpecification implements Specification<Activity> {
    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    private static final String ACTIVITY_CATEGORY_FIELD = "activityCategory";
    private static final String MET = "met";

    @Override
    public Predicate toPredicate(@NonNull Root<Activity> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        ActivityField field = dependencies.getFieldResolver().resolveField(criteria, ActivityField.class);

        return buildPredicateForField(builder, criteria, root, field, query);
    }

    private Predicate buildPredicateForField(CriteriaBuilder builder,
                                             FilterCriteria criteria,
                                             Root<Activity> root,
                                             ActivityField field,
                                             CriteriaQuery<?> query) {
        return switch (field) {
            case CATEGORY -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder, criteria, root, ACTIVITY_CATEGORY_FIELD);
            case MET -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder, criteria, root.get(MET));
            case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    builder, criteria, root, LikesAndSaves.USER_ACTIVITIES.getFieldName());
            case SAVE -> {
                query.groupBy(root.get("id"));
                yield GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                        builder, criteria, root, LikesAndSaves.USER_ACTIVITIES.getFieldName(), null, null);
            }
        };
    }
}

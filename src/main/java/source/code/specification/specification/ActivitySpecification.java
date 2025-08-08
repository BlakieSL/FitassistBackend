package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import source.code.service.declaration.specificationHelpers.SpecificationFetchInitializer;
import source.code.service.declaration.specificationHelpers.SpecificationFieldResolver;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

@AllArgsConstructor(staticName = "of")
public class ActivitySpecification implements Specification<Activity> {
    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    private static final String ACTIVITY_CATEGORY_FIELD = "activityCategory";
    private static final String MET = "met";

    @Override
    public Predicate toPredicate(@NonNull Root<Activity> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        dependencies.getFetchInitializer().initializeFetches(root, ACTIVITY_CATEGORY_FIELD);
        ActivityField field = dependencies.getFieldResolver().resolveField(criteria, ActivityField.class);

        return buildPredicateForField(builder, criteria, root, field, query);
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
                    ACTIVITY_CATEGORY_FIELD
            );
            case ActivityField.MET -> GenericSpecificationHelper.buildPredicateNumericProperty(
                    builder,
                    criteria,
                    root.get(MET)
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

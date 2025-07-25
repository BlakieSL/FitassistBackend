package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.PlanField;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.exercise.Equipment;
import source.code.model.exercise.Exercise;
import source.code.model.plan.Plan;
import source.code.model.plan.PlanCategoryAssociation;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetGroup;

public class PlanSpecification implements Specification<Plan> {
    private final FilterCriteria criteria;

    public PlanSpecification(@NonNull FilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Plan> root, CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        Predicate publicPredicate;
        if (criteria.getIsPublic() != null && !criteria.getIsPublic()) {
            publicPredicate = builder.equal(root.get("user").get("id"), AuthorizationUtil.getUserId());
        } else {
            publicPredicate = builder.isTrue(root.get("isPublic"));
        }

        PlanField field;
        try {
            field = PlanField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException("Invalid filter key: " + criteria.getFilterKey());
        }

        Predicate fieldPredicate = switch (field) {
            case PlanField.TYPE -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    PlanField.TYPE.getFieldName()
            );
            case PlanField.CATEGORY -> GenericSpecificationHelper.buildPredicateJoinProperty(
                    builder,
                    criteria,
                    root,
                    PlanField.CATEGORY.getFieldName(),
                    PlanCategoryAssociation.PLAN_CATEGORY
            );
            case PlanField.LIKE, PlanField.SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    builder,
                    criteria,
                    root,
                    LikesAndSaves.USER_PLANS.getFieldName(),
                    "type",
                    field.getInteractionType()
            );
            case PlanField.EQUIPMENT -> handleEquipmentProperty(root, builder);
        };

        return builder.and(publicPredicate, fieldPredicate);
    }

    private Predicate handleEquipmentProperty(Root<Plan> root, CriteriaBuilder builder) {
        Join<Plan, Workout> workoutJoin = root.join("workouts");
        Join<Workout, WorkoutSetGroup> workoutSetGroupJoin = workoutJoin.join("workoutSetGroups");
        Join<WorkoutSetGroup, WorkoutSet> workoutSetJoin = workoutSetGroupJoin.join("workoutSets");
        Join<WorkoutSet, Exercise> exerciseJoin = workoutSetJoin.join("exercise");
        Join<Exercise, Equipment> equipmentJoin = exerciseJoin.join("equipment");

        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(equipmentJoin.get("id"), criteria.getValue());
            case NOT_EQUAL -> builder.notEqual(equipmentJoin.get("id"), criteria.getValue());
            default -> throw new IllegalStateException(
                    "Unsupported operation: " + criteria.getOperation()
            );
        };
    }


    public static PlanSpecification of(FilterCriteria criteria) {
        return new PlanSpecification(criteria);
    }
}

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
    private static final String PLAN_CATEGORY_ASSOCIATIONS_FIELD = "planCategoryAssociations";
    private static final String PLAN_CATEGORY_FIELD = "planCategory";
    private static final String PLAN_TYPE_FIELD = "planType";
    private static final String USER_FIELD = "user";
    private static final String IS_PUBLIC_FIELD = "isPublic";
    private static final String WORKOUTS_FIELD = "workouts";
    private static final String WORKOUT_SET_GROUPS_FIELD = "workoutSetGroups";
    private static final String WORKOUT_SETS_FIELD = "workoutSets";
    private static final String EXERCISE_FIELD = "exercise";
    private static final String EQUIPMENT_FIELD = "equipment";
    private static final String TYPE_FIELD = "type";
    private static final String ID_FIELD = "id";

    private final transient FilterCriteria criteria;

    public PlanSpecification(@NonNull FilterCriteria criteria) {
        this.criteria = criteria;
    }

    public static PlanSpecification of(@NonNull FilterCriteria criteria) {
        return new PlanSpecification(criteria);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Plan> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        initializeFetches(root);

        Predicate publicPredicate = buildPublicPredicate(root, builder);
        Predicate fieldPredicate = buildPredicateForField(builder, root, resolvePlanField(criteria));

        return builder.and(publicPredicate, fieldPredicate);
    }

    private void initializeFetches(Root<Plan> root) {
        Fetch<Plan, PlanCategoryAssociation> categoryAssociationsFetch =
                root.fetch(PLAN_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);
        categoryAssociationsFetch.fetch(PLAN_CATEGORY_FIELD, JoinType.LEFT);
        root.fetch(PLAN_TYPE_FIELD, JoinType.LEFT);
    }

    private Predicate buildPublicPredicate(Root<Plan> root, CriteriaBuilder builder) {
        if (criteria.getIsPublic() != null && !criteria.getIsPublic()) {
            return builder.equal(
                    root.get(USER_FIELD).get(ID_FIELD),
                    AuthorizationUtil.getUserId()
            );
        }
        return builder.isTrue(root.get(IS_PUBLIC_FIELD));
    }

    private PlanField resolvePlanField(FilterCriteria criteria) {
        try {
            return PlanField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }
    }

    private Predicate buildPredicateForField(CriteriaBuilder builder, Root<Plan> root, PlanField field) {

        return switch (field) {
            case TYPE -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder, criteria, root, PlanField.TYPE.getFieldName());
            case CATEGORY -> buildCategoryPredicate(builder, root);
            case LIKE, SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    builder, criteria, root,
                    LikesAndSaves.USER_PLANS.getFieldName(),
                    TYPE_FIELD, field.getInteractionType());
            case EQUIPMENT -> buildEquipmentPredicate(root, builder);
        };
    }

    private Predicate buildCategoryPredicate(CriteriaBuilder builder, Root<Plan> root) {
        Join<Plan, PlanCategoryAssociation> categoryAssociationJoin =
                root.join(PLAN_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);

        return GenericSpecificationHelper.buildPredicateJoinProperty(
                builder, criteria, categoryAssociationJoin, PlanCategoryAssociation.PLAN_CATEGORY);
    }

    private Predicate buildEquipmentPredicate(Root<Plan> root, CriteriaBuilder builder) {
        Join<Plan, Workout> workoutJoin = root.join(WORKOUTS_FIELD);
        Join<Workout, WorkoutSetGroup> setGroupJoin = workoutJoin.join(WORKOUT_SET_GROUPS_FIELD);
        Join<WorkoutSetGroup, WorkoutSet> setJoin = setGroupJoin.join(WORKOUT_SETS_FIELD);
        Join<WorkoutSet, Exercise> exerciseJoin = setJoin.join(EXERCISE_FIELD);
        Join<Exercise, Equipment> equipmentJoin = exerciseJoin.join(EQUIPMENT_FIELD);

        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(equipmentJoin.get(ID_FIELD), criteria.getValue());
            case NOT_EQUAL -> builder.notEqual(equipmentJoin.get(ID_FIELD), criteria.getValue());
            default -> throw new IllegalStateException(
                    "Unsupported operation: " + criteria.getOperation());
        };
    }
}
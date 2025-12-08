package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterOperationException;
import source.code.exception.InvalidFilterValueException;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.PlanStructureType;
import source.code.helper.Enum.model.field.PlanField;
import source.code.model.exercise.Equipment;
import source.code.model.exercise.Exercise;
import source.code.model.plan.Plan;
import source.code.model.plan.PlanCategoryAssociation;
import source.code.model.user.TypeOfInteraction;
import source.code.model.workout.Workout;
import source.code.model.workout.WorkoutSet;
import source.code.model.workout.WorkoutSetExercise;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;


@AllArgsConstructor(staticName = "of")
public class PlanSpecification implements Specification<Plan> {
    private static final String PLAN_CATEGORY_ASSOCIATIONS_FIELD = "planCategoryAssociations";
    private static final String STRUCTURE_TYPE_FIELD = "planStructureType";
    private static final String USER_FIELD = "user";
    private static final String IS_PUBLIC_FIELD = "isPublic";
    private static final String WORKOUTS_FIELD = "workouts";
    private static final String WORKOUT_SETS_FIELD = "workoutSets";
    private static final String WORKOUT_SET_EXERCISES_FIELD = "workoutSetExercises";
    private static final String EXERCISE_FIELD = "exercise";
    private static final String EQUIPMENT_FIELD = "equipment";
    private static final String TYPE_FIELD = "type";
    private static final String ID_FIELD = "id";

    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    @Override
    public Predicate toPredicate(@NonNull Root<Plan> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        Predicate visibilityPredicate = dependencies.getVisibilityPredicateBuilder()
                .buildVisibilityPredicate(builder, root, criteria, USER_FIELD, ID_FIELD, IS_PUBLIC_FIELD);

        PlanField field = dependencies.getFieldResolver().resolveField(criteria, PlanField.class);
        Predicate fieldPredicate = buildPredicateForField(builder, root, query, field);

        return builder.and(visibilityPredicate, fieldPredicate);
    }

    private Predicate buildPredicateForField(CriteriaBuilder builder, Root<Plan> root, CriteriaQuery<?> query, PlanField field) {
        return switch (field) {
            case STRUCTURE_TYPE -> buildStructureTypePredicate(builder, root);
            case CATEGORY -> buildCategoryPredicate(builder, root);
            case EQUIPMENT -> buildEquipmentPredicate(root, builder, query);
            case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder, criteria, root, USER_FIELD);
            case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    builder, criteria, root, LikesAndSaves.USER_PLANS.getFieldName(),
                    TYPE_FIELD, TypeOfInteraction.SAVE);
            case LIKED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    builder, criteria, root, LikesAndSaves.USER_PLANS.getFieldName(),
                    TYPE_FIELD, TypeOfInteraction.LIKE);
            case DISLIKED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    builder, criteria, root, LikesAndSaves.USER_PLANS.getFieldName(),
                    TYPE_FIELD, TypeOfInteraction.DISLIKE);
            case SAVE -> buildInteractionPredicate(builder, root, TypeOfInteraction.SAVE);
            case LIKE -> buildInteractionPredicate(builder, root, TypeOfInteraction.LIKE);
        };
    }

    private Predicate buildCategoryPredicate(CriteriaBuilder builder, Root<Plan> root) {
        Join<Plan, PlanCategoryAssociation> categoryAssociationJoin =
                root.join(PLAN_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);

        return GenericSpecificationHelper.buildPredicateJoinProperty(
                builder, criteria, categoryAssociationJoin, PlanCategoryAssociation.PLAN_CATEGORY);
    }

    private Predicate buildEquipmentPredicate(Root<Plan> root, CriteriaBuilder builder, CriteriaQuery<?> query) {
        int equipmentId = GenericSpecificationHelper.validateAndGetId(criteria);

        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<Plan> subRoot = subquery.from(Plan.class);
        Join<Plan, Workout> workoutJoin = subRoot.join(WORKOUTS_FIELD);
        Join<Workout, WorkoutSet> workoutSetJoin = workoutJoin.join(WORKOUT_SETS_FIELD);
        Join<WorkoutSet, WorkoutSetExercise> workoutSetExerciseJoin = workoutSetJoin.join(WORKOUT_SET_EXERCISES_FIELD);
        Join<WorkoutSetExercise, Exercise> exerciseJoin = workoutSetExerciseJoin.join(EXERCISE_FIELD);

        subquery.select(subRoot.get(ID_FIELD))
                .where(builder.and(
                        builder.equal(subRoot.get(ID_FIELD), root.get(ID_FIELD)),
                        builder.equal(exerciseJoin.get(EQUIPMENT_FIELD).get(ID_FIELD), equipmentId)));

        return switch (criteria.getOperation()) {
            case EQUAL -> builder.exists(subquery);
            case NOT_EQUAL -> builder.not(builder.exists(subquery));
            default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
        };
    }

    private Predicate buildInteractionPredicate(CriteriaBuilder builder, Root<Plan> root, TypeOfInteraction interactionType) {
        return GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                builder, criteria, root,
                LikesAndSaves.USER_PLANS.getFieldName(),
                TYPE_FIELD, interactionType);
    }

    private Predicate buildStructureTypePredicate(CriteriaBuilder builder, Root<Plan> root) {
        PlanStructureType structureType = parseStructureType(criteria.getValue());
        Path<PlanStructureType> path = root.get(STRUCTURE_TYPE_FIELD);

        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(path, structureType);
            case NOT_EQUAL -> builder.notEqual(path, structureType);
            default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
        };
    }

    private PlanStructureType parseStructureType(Object value) {
        try {
            return PlanStructureType.valueOf(value.toString());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterValueException(value.toString());
        }
    }
}

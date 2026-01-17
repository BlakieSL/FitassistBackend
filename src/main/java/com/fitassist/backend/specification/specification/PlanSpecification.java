package com.fitassist.backend.specification.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.exception.InvalidFilterOperationException;
import com.fitassist.backend.exception.InvalidFilterValueException;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.plan.PlanCategoryAssociation;
import com.fitassist.backend.model.plan.PlanStructureType;
import com.fitassist.backend.model.user.TypeOfInteraction;
import com.fitassist.backend.model.workout.Workout;
import com.fitassist.backend.model.workout.WorkoutSet;
import com.fitassist.backend.model.workout.WorkoutSetExercise;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.PredicateContext;
import com.fitassist.backend.specification.specification.field.LikesAndSaves;
import com.fitassist.backend.specification.specification.field.PlanField;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;

import static com.fitassist.backend.specification.SpecificationConstants.*;

public class PlanSpecification extends AbstractSpecification<Plan, PlanField> {

	private static final String PLAN_CATEGORY_ASSOCIATIONS_FIELD = "planCategoryAssociations";

	private static final String STRUCTURE_TYPE_FIELD = "planStructureType";

	private static final String WORKOUTS_FIELD = "workouts";

	private static final String WORKOUT_SETS_FIELD = "workoutSets";

	private static final String WORKOUT_SET_EXERCISES_FIELD = "workoutSetExercises";

	private static final String EXERCISE_FIELD = "exercise";

	private static final String EQUIPMENT_FIELD = "equipment";

	public PlanSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		super(criteria, dependencies);
	}

	@Override
	protected Class<PlanField> getFieldClass() {
		return PlanField.class;
	}

	@Override
	public Predicate toPredicate(@NotNull Root<Plan> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
		Predicate visibilityPredicate = dependencies.getVisibilityPredicateBuilder()
			.buildVisibilityPredicate(builder, root, criteria, USER_FIELD, ID_FIELD, IS_PUBLIC_FIELD);

		if (criteria.getFilterKey() == null || criteria.getFilterKey().isEmpty()) {
			return visibilityPredicate;
		}

		PlanField field = dependencies.getFieldResolver().resolveField(criteria, PlanField.class);
		PredicateContext<Plan> context = new PredicateContext<>(builder, root, query, criteria);
		Predicate fieldPredicate = buildPredicateForField(context, field);

		return builder.and(visibilityPredicate, fieldPredicate);
	}

	@Override
	protected Predicate buildPredicateForField(PredicateContext<Plan> context, PlanField field) {
		return switch (field) {
			case STRUCTURE_TYPE -> buildStructureTypePredicate(context);
			case CATEGORY -> buildCategoryPredicate(context);
			case EQUIPMENT -> buildEquipmentPredicate(context);
			case CREATED_BY_USER -> GenericSpecificationHelper.buildPredicateEntityProperty(context, USER_FIELD);
			case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_PLANS.getFieldName(), TYPE_FIELD, TypeOfInteraction.SAVE);
			case LIKED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_PLANS.getFieldName(), TYPE_FIELD, TypeOfInteraction.LIKE);
			case DISLIKED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(context,
					LikesAndSaves.USER_PLANS.getFieldName(), TYPE_FIELD, TypeOfInteraction.DISLIKE);
			case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
					LikesAndSaves.USER_PLANS.getFieldName(), TYPE_FIELD, TypeOfInteraction.SAVE);
			case LIKE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(context,
					LikesAndSaves.USER_PLANS.getFieldName(), TYPE_FIELD, TypeOfInteraction.LIKE);
		};
	}

	private Predicate buildCategoryPredicate(PredicateContext<Plan> context) {
		Join<Plan, PlanCategoryAssociation> categoryAssociationJoin = context.root()
			.join(PLAN_CATEGORY_ASSOCIATIONS_FIELD, JoinType.LEFT);

		return GenericSpecificationHelper.buildPredicateJoinProperty(context, categoryAssociationJoin,
				PlanCategoryAssociation.PLAN_CATEGORY);
	}

	private Predicate buildEquipmentPredicate(PredicateContext<Plan> context) {
		int equipmentId = GenericSpecificationHelper.validateAndGetId(context.criteria());

		Subquery<Integer> subquery = context.query().subquery(Integer.class);
		Root<Plan> subRoot = subquery.from(Plan.class);
		Join<Plan, Workout> workoutJoin = subRoot.join(WORKOUTS_FIELD);
		Join<Workout, WorkoutSet> workoutSetJoin = workoutJoin.join(WORKOUT_SETS_FIELD);
		Join<WorkoutSet, WorkoutSetExercise> workoutSetExerciseJoin = workoutSetJoin.join(WORKOUT_SET_EXERCISES_FIELD);
		Join<WorkoutSetExercise, Exercise> exerciseJoin = workoutSetExerciseJoin.join(EXERCISE_FIELD);

		subquery.select(subRoot.get(ID_FIELD))
			.where(context.builder()
				.and(context.builder().equal(subRoot.get(ID_FIELD), context.root().get(ID_FIELD)),
						context.builder().equal(exerciseJoin.get(EQUIPMENT_FIELD).get(ID_FIELD), equipmentId)));

		return switch (context.criteria().getOperation()) {
			case EQUAL -> context.builder().exists(subquery);
			case NOT_EQUAL -> context.builder().not(context.builder().exists(subquery));
			default -> throw new InvalidFilterOperationException(context.criteria().getOperation().name());
		};
	}

	private Predicate buildStructureTypePredicate(PredicateContext<Plan> context) {
		PlanStructureType structureType = parseStructureType(context.criteria().getValue());
		Path<PlanStructureType> path = context.root().get(STRUCTURE_TYPE_FIELD);

		return switch (context.criteria().getOperation()) {
			case EQUAL -> context.builder().equal(path, structureType);
			case NOT_EQUAL -> context.builder().notEqual(path, structureType);
			default -> throw new InvalidFilterOperationException(context.criteria().getOperation().name());
		};
	}

	private PlanStructureType parseStructureType(Object value) {
		try {
			return PlanStructureType.valueOf(value.toString());
		}
		catch (IllegalArgumentException e) {
			throw new InvalidFilterValueException(value.toString());
		}
	}

}

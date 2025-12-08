package source.code.specification.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ExerciseField;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

public class ExerciseSpecification extends AbstractSpecification<Exercise, ExerciseField> {

    private static final String EXERCISE_TARGET_MUSCLES_FIELD = "exerciseTargetMuscles";
    private static final String TARGET_MUSCLE_FIELD = "targetMuscle";
    private static final String EQUIPMENT_FIELD = "equipment";
    private static final String EXPERTISE_LEVEL_FIELD = "expertiseLevel";
    private static final String FORCE_TYPE_FIELD = "forceType";
    private static final String MECHANICS_TYPE_FIELD = "mechanicsType";

    public ExerciseSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
        super(criteria, dependencies);
    }

    @Override
    protected Class<ExerciseField> getFieldClass() {
        return ExerciseField.class;
    }

    @Override
    protected Predicate buildPredicateForField(PredicateContext<Exercise> context, ExerciseField field) {
        return switch (field) {
            case EXPERTISE_LEVEL -> GenericSpecificationHelper.buildPredicateEntityProperty(context, EXPERTISE_LEVEL_FIELD);
            case EQUIPMENT -> GenericSpecificationHelper.buildPredicateEntityProperty(context, EQUIPMENT_FIELD);
            case MECHANICS_TYPE -> GenericSpecificationHelper.buildPredicateEntityProperty(context, MECHANICS_TYPE_FIELD);
            case FORCE_TYPE -> GenericSpecificationHelper.buildPredicateEntityProperty(context, FORCE_TYPE_FIELD);
            case TARGET_MUSCLE -> buildTargetMusclePredicate(context);
            case SAVED_BY_USER -> GenericSpecificationHelper.buildSavedByUserPredicate(
                    context, LikesAndSaves.USER_EXERCISES.getFieldName());
            case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    context, LikesAndSaves.USER_EXERCISES.getFieldName(), null, null);
        };
    }

    private Predicate buildTargetMusclePredicate(PredicateContext<Exercise> context) {
        Join<Exercise, ExerciseTargetMuscle> targetMuscleJoin =
                context.root().join(EXERCISE_TARGET_MUSCLES_FIELD, JoinType.LEFT);

        return GenericSpecificationHelper.buildPredicateJoinProperty(context, targetMuscleJoin, TARGET_MUSCLE_FIELD);
    }
}

package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.helper.Enum.model.field.ExerciseField;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

@AllArgsConstructor(staticName = "of")
public class ExerciseSpecification implements Specification<Exercise> {
    private final FilterCriteria criteria;
    private final SpecificationDependencies dependencies;

    private static final String EXERCISE_TARGET_MUSCLES_FIELD = "exerciseTargetMuscles";
    private static final String TARGET_MUSCLE_FIELD = "targetMuscle";
    private static final String EQUIPMENT_FIELD = "equipment";
    private static final String EXPERTISE_LEVEL_FIELD = "expertiseLevel";
    private static final String FORCE_TYPE_FIELD = "forceType";
    private static final String MECHANICS_TYPE_FIELD = "mechanicsType";

    @Override
    public Predicate toPredicate(@NonNull Root<Exercise> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        dependencies.getFetchInitializer().initializeFetches(root, EQUIPMENT_FIELD, EXPERTISE_LEVEL_FIELD, FORCE_TYPE_FIELD, MECHANICS_TYPE_FIELD);
        initializeComplexFetches(root);

        ExerciseField field = dependencies.getFieldResolver().resolveField(criteria, ExerciseField.class);

        return buildPredicateForField(builder, criteria, root, field);
    }

    private void initializeComplexFetches(Root<Exercise> root) {
        Fetch<Exercise, ExerciseTargetMuscle> targetMuscleFetch =
                root.fetch(EXERCISE_TARGET_MUSCLES_FIELD, JoinType.LEFT);
        targetMuscleFetch.fetch(TARGET_MUSCLE_FIELD, JoinType.LEFT);
    }

    private Predicate buildPredicateForField(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<Exercise> root,
            ExerciseField field) {

        return switch (field) {
            case EXPERTISE_LEVEL -> buildPredicateForEntityProperty(
                    builder, criteria, root, EXPERTISE_LEVEL_FIELD);
            case EQUIPMENT -> buildPredicateForEntityProperty(
                    builder, criteria, root, EQUIPMENT_FIELD);
            case MECHANICS_TYPE -> buildPredicateForEntityProperty(
                    builder, criteria, root, MECHANICS_TYPE_FIELD);
            case FORCE_TYPE -> buildPredicateForEntityProperty(
                    builder, criteria, root, FORCE_TYPE_FIELD);
            case TARGET_MUSCLE -> buildPredicateForTargetMuscle(
                    builder, criteria, root);
            case SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    builder,
                    criteria,
                    root,
                    LikesAndSaves.USER_EXERCISES.getFieldName(),
                    null,
                    null);
        };
    }

    private Predicate buildPredicateForEntityProperty(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<?> root,
            String property) {
        return GenericSpecificationHelper.buildPredicateEntityProperty(builder, criteria, root, property);
    }

    private Predicate buildPredicateForTargetMuscle(CriteriaBuilder builder, FilterCriteria criteria, Root<Exercise> root) {
        Join<Exercise, ExerciseTargetMuscle> targetMuscleJoin = root.join(EXERCISE_TARGET_MUSCLES_FIELD, JoinType.LEFT);

        return GenericSpecificationHelper
                .buildPredicateJoinProperty(builder, criteria, targetMuscleJoin, TARGET_MUSCLE_FIELD);
    }
}

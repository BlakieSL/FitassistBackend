package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.helper.Enum.model.field.ExerciseField;
import source.code.helper.Enum.model.LikesAndSaves;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;

public class ExerciseSpecification implements Specification<Exercise> {
    private final FilterCriteria criteria;

    public ExerciseSpecification(FilterCriteria criteria) {
        this.criteria = criteria;
    }

    public static ExerciseSpecification of(@NonNull FilterCriteria criteria) {
        return new ExerciseSpecification(criteria);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Exercise> root, CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        ExerciseField field;
        try {
            field = ExerciseField.valueOf(criteria.getFilterKey());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterKeyException(criteria.getFilterKey());
        }

        return switch (field) {
            case  ExerciseField.EXPERTISE_LEVEL -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    ExerciseField.EXPERTISE_LEVEL.getFieldName()
            );
            case ExerciseField.EQUIPMENT -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    ExerciseField.EQUIPMENT.getFieldName()
            );
            case ExerciseField.MECHANICS_TYPE -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    ExerciseField.MECHANICS_TYPE.getFieldName()
            );
            case ExerciseField.FORCE_TYPE -> GenericSpecificationHelper.buildPredicateEntityProperty(
                    builder,
                    criteria,
                    root,
                    ExerciseField.FORCE_TYPE.getFieldName()
            );
            case ExerciseField.TARGET_MUSCLE -> GenericSpecificationHelper.buildPredicateJoinProperty(
                    builder,
                    criteria,
                    root,
                    ExerciseField.TARGET_MUSCLE.getFieldName(),
                    ExerciseTargetMuscle.TARGET_MUSCLE
            );
            case ExerciseField.SAVE -> GenericSpecificationHelper.buildPredicateUserEntityInteractionRange(
                    builder,
                    criteria,
                    root,
                    LikesAndSaves.USER_EXERCISES.getFieldName(),
                    null,
                    null
            );
        };
    }
}

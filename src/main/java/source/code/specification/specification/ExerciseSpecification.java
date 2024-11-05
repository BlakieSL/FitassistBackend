package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;
import source.code.dto.POJO.FilterCriteria;
import source.code.helper.Enum.Model.ExerciseField;
import source.code.helper.Enum.Model.LikesAndSaves;
import source.code.helper.TriFunction;
import source.code.model.exercise.Exercise;
import source.code.model.exercise.ExerciseTargetMuscle;

import java.util.Map;
import java.util.Optional;

public class ExerciseSpecification extends BaseSpecification<Exercise> {
    private final Map<String, TriFunction<Root<Exercise>,
            CriteriaQuery<?>, CriteriaBuilder, Predicate>> fieldHandlers;

    public ExerciseSpecification(@NonNull FilterCriteria criteria) {
        super(criteria);

        fieldHandlers = Map.of(
                ExerciseField.EXPERTISE_LEVEL.name(),
                (root, query, builder) -> handleEntityProperty(
                        root,
                        ExerciseField.EXPERTISE_LEVEL.getFieldName(),
                        builder
                ),
                ExerciseField.EQUIPMENT.name(),
                (root, query, builder) -> handleEntityProperty(
                        root,
                        ExerciseField.EQUIPMENT.getFieldName(),
                        builder
                ),
                ExerciseField.TYPE.name(),
                (root, query, builder) -> handleEntityProperty(
                        root,
                        ExerciseField.TYPE.getFieldName(),
                        builder
                ),
                ExerciseField.MECHANICS_TYPE.name(),
                (root, query, builder) -> handleEntityProperty(
                        root,
                        ExerciseField.MECHANICS_TYPE.getFieldName(),
                        builder
                ),
                ExerciseField.FORCE_TYPE.name(),
                (root, query, builder) -> handleEntityProperty(
                        root,
                        ExerciseField.FORCE_TYPE.getFieldName(),
                        builder
                ),
                ExerciseField.TARGET_MUSCLE.name(),
                (root, query, builder) -> handleManyToManyProperty(
                        root,
                        ExerciseField.TARGET_MUSCLE.getFieldName(),
                        ExerciseTargetMuscle.TARGET_MUSCLE,
                        builder
                ),
                LikesAndSaves.LIKES.name(),
                (root, query, builder) -> handleLikesProperty(
                        root,
                        LikesAndSaves.USER_EXERCISES.getFieldName(),
                        query,
                        builder
                ),
                LikesAndSaves.SAVES.name(),
                (root, query, builder) -> handleSavesProperty(
                        root,
                        LikesAndSaves.USER_EXERCISES.getFieldName(),
                        query,
                        builder
                )
        );
    }

    public static ExerciseSpecification of(@NonNull FilterCriteria criteria) {
        return new ExerciseSpecification(criteria);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Exercise> root, @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        return Optional.ofNullable(fieldHandlers.get(criteria.getFilterKey()))
                .map(handler -> handler.apply(root, query, builder))
                .orElseThrow(() -> new IllegalStateException("Unexpected filter key: " + criteria.getFilterKey()));
    }
}

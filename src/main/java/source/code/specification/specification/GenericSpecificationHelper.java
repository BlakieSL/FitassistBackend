package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterOperationException;
import source.code.exception.InvalidFilterValueException;

import java.math.BigDecimal;

public class GenericSpecificationHelper {

    private GenericSpecificationHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> Predicate buildPredicateEntityProperty(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<T> root,
            String joinProperty
    ) {
        int value = validateAndGetId(criteria);
        return buildIdBasedPredicate(builder, criteria, root.join(joinProperty).get("id"), value);
    }

    public static <T> Predicate buildPredicateJoinProperty(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<T> root,
            String joinProperty,
            String subJoinProperty
    ) {
        Object value = validateAndGetId(criteria);
        return buildIdBasedPredicate(
                builder,
                criteria,
                root.join(joinProperty).join(subJoinProperty).get("id"),
                value
        );
    }

    public static <T> Predicate buildPredicateNumericProperty(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Path<BigDecimal> path
    ) {
        BigDecimal value = validateAndGetBigDecimal(criteria);
        return switch (criteria.getOperation()) {
            case GREATER_THAN -> builder.greaterThan(path, value);
            case LESS_THAN -> builder.lessThan(path, value);
            case EQUAL -> builder.equal(path, value);
            case NOT_EQUAL -> builder.notEqual(path, value);
            default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
        };
    }

    public static <T> Predicate buildPredicateUserEntityInteractionRange(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<T> root,
            String joinProperty,
            String targetTypeFieldName,
            Object typeValue
    ) {
        Join<T, Object> userEntityJoin = root.join(joinProperty, JoinType.LEFT);

        Predicate predicate = builder.conjunction();
        if (typeValue != null) {
            Predicate typeFilter = builder.or(
                    builder.isNull(userEntityJoin.get(targetTypeFieldName)),
                    builder.equal(userEntityJoin.get(targetTypeFieldName), typeValue)
            );
            predicate = builder.and(predicate, typeFilter);
        }

        Expression<Long> countExpression = builder.coalesce(
                builder.count(userEntityJoin.get("id")),
                0L
        );

        Predicate rangePredicate = createRangePredicate(countExpression, builder, criteria);
        return builder.and(predicate, rangePredicate);
    }

    private static Predicate createRangePredicate(Expression<Long> countExpression, CriteriaBuilder builder,
                                                  FilterCriteria criteria) {
        Long longValue = validateAndGetLong(criteria);
        return switch (criteria.getOperation()) {
            case GREATER_THAN -> builder.greaterThan(countExpression, longValue);
            case LESS_THAN -> builder.lessThan(countExpression, longValue);
            case EQUAL -> builder.equal(countExpression, longValue);
            case NOT_EQUAL -> builder.notEqual(countExpression, longValue);
            default -> throw new InvalidFilterValueException(criteria.getOperation().toString());
        };
    }


    private static int validateAndGetId(FilterCriteria criteria) {
        Object value = criteria.getValue();
        try {
            return ((Number) value).intValue();
        } catch (ClassCastException | NullPointerException e) {
            throw new InvalidFilterValueException(criteria.getValue().toString());
        }
    }

    private static BigDecimal validateAndGetBigDecimal(FilterCriteria criteria) {
        Object value = criteria.getValue();
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        throw new InvalidFilterValueException(value + " , class: " + value.getClass().getName());
    }

    private static Long validateAndGetLong(FilterCriteria criteria) {
        Object value = criteria.getValue();
        try {
            return ((Number) value).longValue();
        } catch (ClassCastException e) {
            throw new InvalidFilterValueException(criteria.getValue().toString());
        }
    }

    private static Predicate buildIdBasedPredicate(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Path<Integer> idPath,
            Object value
    ) {
        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(idPath, value);
            case NOT_EQUAL -> builder.notEqual(idPath, value);
            default -> throw new InvalidFilterOperationException(criteria.getOperation().toString());
        };
    }
}
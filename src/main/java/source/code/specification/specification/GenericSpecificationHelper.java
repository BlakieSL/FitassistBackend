package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterOperationException;
import source.code.exception.InvalidFilterValueException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public static <T> Predicate   buildPredicateUserEntityInteractionRange(
            CriteriaBuilder builder,
            FilterCriteria criteria,
            Root<T> root,
            String joinProperty,
            String targetTypeFieldName,
            Object typeValue
    ) {
        Subquery<Long> subquery = builder.createQuery(Long.class).subquery(Long.class);
        Root<T> subRoot = subquery.from(root.getModel().getJavaType());

        Join<T, Object> subJoin = subRoot.join(joinProperty, JoinType.LEFT);

        List<Predicate> subqueryPredicates = new ArrayList<>();
        subqueryPredicates.add(builder.equal(subRoot.get("id"), root.get("id")));

        if (typeValue != null && targetTypeFieldName != null) {

            if (typeValue instanceof Enum<?>) {
                subqueryPredicates.add(builder.equal(
                        subJoin.get(targetTypeFieldName),
                        typeValue
                ));
            }

            else {
                subqueryPredicates.add(builder.equal(
                        subJoin.get(targetTypeFieldName),
                        typeValue.toString()
                ));
            }
        }

        subquery.where(subqueryPredicates.toArray(new Predicate[0]));
        subquery.select(builder.count(subJoin));

        return createRangePredicate(subquery, builder, criteria);
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
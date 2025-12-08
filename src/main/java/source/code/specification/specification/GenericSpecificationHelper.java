package source.code.specification.specification;

import jakarta.persistence.criteria.*;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterOperationException;
import source.code.exception.InvalidFilterValueException;

import java.math.BigDecimal;
import java.util.ArrayList;

public class GenericSpecificationHelper {

    private GenericSpecificationHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> Predicate buildPredicateEntityProperty(CriteriaBuilder builder,
                                                             FilterCriteria criteria,
                                                             Root<T> root,
                                                             String joinProperty) {
        var value = validateAndGetId(criteria);
        return buildIdBasedPredicate(builder, criteria, root.get(joinProperty).get("id"), value);
    }

    public static <T, J> Predicate buildPredicateJoinProperty(CriteriaBuilder builder,
                                                              FilterCriteria criteria,
                                                              Join<T, J> join,
                                                              String subJoinProperty) {
        var value = validateAndGetId(criteria);
        return buildCollectionIdBasedPredicate(builder, criteria, join, subJoinProperty, value);
    }

    public static <T> Predicate buildPredicateNumericProperty(CriteriaBuilder builder,
                                                              FilterCriteria criteria,
                                                              Path<BigDecimal> path) {
        var value = validateAndGetBigDecimal(criteria);
        return buildNumericBasedPredicate(builder, criteria, path, value);
    }

    public static <T> Predicate buildSavedByUserPredicate(CriteriaBuilder builder,
                                                          FilterCriteria criteria,
                                                          Root<T> root,
                                                          String userEntityJoinField) {
        return buildSavedByUserPredicate(builder, criteria, root, userEntityJoinField, null, null);
    }

    public static <T> Predicate buildSavedByUserPredicate(CriteriaBuilder builder,
                                                          FilterCriteria criteria,
                                                          Root<T> root,
                                                          String userEntityJoinField,
                                                          String typeFieldName,
                                                          Object typeValue) {
        var userId = validateAndGetId(criteria);
        var userEntityJoin = root.join(userEntityJoinField, JoinType.INNER);
        var userPredicate = builder.equal(userEntityJoin.get("user").get("id"), userId);

        if (typeFieldName != null && typeValue != null) {
            var typePredicate = builder.equal(userEntityJoin.get(typeFieldName), typeValue);
            return builder.and(userPredicate, typePredicate);
        }
        return userPredicate;
    }

    public static <T> Predicate buildPredicateUserEntityInteractionRange(CriteriaBuilder builder,
                                                                         FilterCriteria criteria,
                                                                         Root<T> root,
                                                                         String joinProperty,
                                                                         String targetTypeFieldName,
                                                                         Object typeValue) {
        var subquery = builder.createQuery(Long.class).subquery(Long.class);
        var subRoot = subquery.from(root.getModel().getJavaType());
        var subJoin = subRoot.join(joinProperty, JoinType.LEFT);
        var subqueryPredicates = new ArrayList<Predicate>();

        subqueryPredicates.add(builder.equal(subRoot.get("id"), root.get("id")));

        if (typeValue != null && targetTypeFieldName != null) {
            if (typeValue instanceof Enum<?>) {
                subqueryPredicates.add(builder.equal(subJoin.get(targetTypeFieldName), typeValue));
            } else {
                subqueryPredicates.add(builder.equal(subJoin.get(targetTypeFieldName), typeValue.toString()));
            }
        }

        subquery.where(subqueryPredicates.toArray(new Predicate[0]));
        subquery.select(builder.count(subJoin));

        return createRangePredicate(subquery, builder, criteria);
    }

    private static Predicate createRangePredicate(Expression<Long> countExpression,
                                                  CriteriaBuilder builder,
                                                  FilterCriteria criteria) {
        var value = validateAndGetLong(criteria);
        return switch (criteria.getOperation()) {
            case GREATER_THAN -> builder.greaterThan(countExpression, value);
            case GREATER_THAN_EQUAL -> builder.greaterThanOrEqualTo(countExpression, value);
            case LESS_THAN -> builder.lessThan(countExpression, value);
            case LESS_THAN_EQUAL -> builder.lessThanOrEqualTo(countExpression, value);
            default -> throw new InvalidFilterValueException(criteria.getOperation().toString());
        };
    }

    public static int validateAndGetId(FilterCriteria criteria) {
        var value = criteria.getValue();
        try {
            return ((Number) value).intValue();
        } catch (ClassCastException | NullPointerException e) {
            throw new InvalidFilterValueException(criteria.getValue().toString());
        }
    }

    private static BigDecimal validateAndGetBigDecimal(FilterCriteria criteria) {
        var value = criteria.getValue();
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        throw new InvalidFilterValueException(value + " , class: " + value.getClass().getName());
    }

    private static Long validateAndGetLong(FilterCriteria criteria) {
        var value = criteria.getValue();
        try {
            return ((Number) value).longValue();
        } catch (ClassCastException e) {
            throw new InvalidFilterValueException(criteria.getValue().toString());
        }
    }

    private static Predicate buildNumericBasedPredicate(CriteriaBuilder builder,
                                                        FilterCriteria criteria,
                                                        Path<BigDecimal> path,
                                                        BigDecimal value) {
        return switch (criteria.getOperation()) {
            case GREATER_THAN -> builder.greaterThan(path, value);
            case GREATER_THAN_EQUAL -> builder.greaterThanOrEqualTo(path, value);
            case LESS_THAN -> builder.lessThan(path, value);
            case LESS_THAN_EQUAL -> builder.lessThanOrEqualTo(path, value);
            default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
        };
    }

    private static Predicate buildIdBasedPredicate(CriteriaBuilder builder,
                                                   FilterCriteria criteria,
                                                   Path<Integer> idPath,
                                                   Object value) {
        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(idPath, value);
            case NOT_EQUAL -> builder.notEqual(idPath, value);
            default -> throw new InvalidFilterOperationException(criteria.getOperation().toString());
        };
    }

    private static <T, J> Predicate buildCollectionIdBasedPredicate(CriteriaBuilder builder,
                                                                    FilterCriteria criteria,
                                                                    Join<T, J> join,
                                                                    String subJoinProperty,
                                                                    Object value) {
        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(join.get(subJoinProperty).get("id"), value);
            case NOT_EQUAL -> {
                join.on(builder.equal(join.get(subJoinProperty).get("id"), value));
                yield builder.isNull(join.get("id"));
            }
            default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
        };
    }
}

package source.code.specification.specification;

import static source.code.specification.SpecificationConstants.ID_FIELD;
import static source.code.specification.SpecificationConstants.USER_FIELD;

import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterOperationException;
import source.code.exception.InvalidFilterValueException;
import source.code.specification.PredicateContext;

public class GenericSpecificationHelper {

	private GenericSpecificationHelper() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static <T> Predicate buildPredicateEntityProperty(PredicateContext<T> context, String joinProperty) {
		var value = validateAndGetId(context.criteria());
		return buildIdBasedPredicate(context.builder(), context.criteria(),
			context.root().get(joinProperty).get(ID_FIELD), value);
	}

	public static <T, J> Predicate buildPredicateJoinProperty(PredicateContext<T> context, Join<T, J> join,
															  String subJoinProperty) {
		var value = validateAndGetId(context.criteria());
		return buildCollectionIdBasedPredicate(context.builder(), context.criteria(), join, subJoinProperty, value);
	}

	public static <T> Predicate buildPredicateNumericProperty(PredicateContext<T> context, Path<BigDecimal> path) {
		var value = validateAndGetBigDecimal(context.criteria());
		return buildNumericBasedPredicate(context.builder(), context.criteria(), path, value);
	}

	public static <T> Predicate buildSavedByUserPredicate(PredicateContext<T> context, String userEntityJoinField) {
		return buildSavedByUserPredicate(context, userEntityJoinField, null, null);
	}

	public static <T> Predicate buildSavedByUserPredicate(PredicateContext<T> context, String userEntityJoinField,
														  String typeFieldName, Object typeValue) {
		var userId = validateAndGetId(context.criteria());
		var userEntityJoin = context.root().join(userEntityJoinField, JoinType.INNER);
		var userPredicate = context.builder().equal(userEntityJoin.get(USER_FIELD).get(ID_FIELD), userId);

		if (typeFieldName != null && typeValue != null) {
			var typePredicate = context.builder().equal(userEntityJoin.get(typeFieldName), typeValue);
			return context.builder().and(userPredicate, typePredicate);
		}
		return userPredicate;
	}

	public static <T> Predicate buildPredicateUserEntityInteractionRange(PredicateContext<T> context,
																		 String joinProperty, String targetTypeFieldName, Object typeValue) {
		var subquery = context.builder().createQuery(Long.class).subquery(Long.class);
		var subRoot = subquery.from(context.root().getModel().getJavaType());
		var subJoin = subRoot.join(joinProperty, JoinType.LEFT);
		var subqueryPredicates = new ArrayList<Predicate>();

		subqueryPredicates.add(context.builder().equal(subRoot.get(ID_FIELD), context.root().get(ID_FIELD)));

		if (typeValue != null && targetTypeFieldName != null) {
			if (typeValue instanceof Enum<?>) {
				subqueryPredicates.add(context.builder().equal(subJoin.get(targetTypeFieldName), typeValue));
			} else {
				subqueryPredicates.add(context.builder().equal(subJoin.get(targetTypeFieldName), typeValue.toString()));
			}
		}

		subquery.where(subqueryPredicates.toArray(new Predicate[0]));
		subquery.select(context.builder().count(subJoin));

		return createRangePredicate(subquery, context.builder(), context.criteria());
	}

	private static Predicate createRangePredicate(Expression<Long> countExpression, CriteriaBuilder builder,
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

	private static Predicate buildNumericBasedPredicate(CriteriaBuilder builder, FilterCriteria criteria,
														Path<BigDecimal> path, BigDecimal value) {
		return switch (criteria.getOperation()) {
			case GREATER_THAN -> builder.greaterThan(path, value);
			case GREATER_THAN_EQUAL -> builder.greaterThanOrEqualTo(path, value);
			case LESS_THAN -> builder.lessThan(path, value);
			case LESS_THAN_EQUAL -> builder.lessThanOrEqualTo(path, value);
			default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
		};
	}

	private static Predicate buildIdBasedPredicate(CriteriaBuilder builder, FilterCriteria criteria,
												   Path<Integer> idPath, Object value) {
		return switch (criteria.getOperation()) {
			case EQUAL -> builder.equal(idPath, value);
			case NOT_EQUAL -> builder.notEqual(idPath, value);
			default -> throw new InvalidFilterOperationException(criteria.getOperation().toString());
		};
	}

	private static <T, J> Predicate buildCollectionIdBasedPredicate(CriteriaBuilder builder, FilterCriteria criteria,
																	Join<T, J> join, String subJoinProperty, Object value) {
		return switch (criteria.getOperation()) {
			case EQUAL -> builder.equal(join.get(subJoinProperty).get(ID_FIELD), value);
			case NOT_EQUAL -> {
				join.on(builder.equal(join.get(subJoinProperty).get(ID_FIELD), value));
				yield builder.isNull(join.get(ID_FIELD));
			}
			default -> throw new InvalidFilterOperationException(criteria.getOperation().name());
		};
	}

}

package com.fitassist.backend.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.PredicateContext;

public abstract class AbstractSpecification<T, F extends Enum<F>> implements Specification<T> {

	protected final FilterCriteria criteria;

	protected final SpecificationDependencies dependencies;

	protected AbstractSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		this.criteria = criteria;
		this.dependencies = dependencies;
	}

	@Override
	public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
		if (criteria.getFilterKey() == null || criteria.getFilterKey().isEmpty()) {
			return builder.conjunction();
		}
		F field = dependencies.getFieldResolver().resolveField(criteria, getFieldClass());
		PredicateContext<T> context = new PredicateContext<>(builder, root, query, criteria);
		return buildPredicateForField(context, field);
	}

	protected abstract Class<F> getFieldClass();

	protected abstract Predicate buildPredicateForField(PredicateContext<T> context, F field);

}

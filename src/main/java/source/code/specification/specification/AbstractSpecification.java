package source.code.specification.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import source.code.dto.pojo.FilterCriteria;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;
import source.code.specification.PredicateContext;

public abstract class AbstractSpecification<T, F extends Enum<F>> implements Specification<T> {

	protected final FilterCriteria criteria;

	protected final SpecificationDependencies dependencies;

	protected AbstractSpecification(FilterCriteria criteria, SpecificationDependencies dependencies) {
		this.criteria = criteria;
		this.dependencies = dependencies;
	}

	@Override
	public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
		F field = dependencies.getFieldResolver().resolveField(criteria, getFieldClass());
		PredicateContext<T> context = new PredicateContext<>(builder, root, query, criteria);
		return buildPredicateForField(context, field);
	}

	protected abstract Class<F> getFieldClass();

	protected abstract Predicate buildPredicateForField(PredicateContext<T> context, F field);

}

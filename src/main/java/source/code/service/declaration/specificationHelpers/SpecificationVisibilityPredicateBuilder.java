package source.code.service.declaration.specificationHelpers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import source.code.dto.pojo.FilterCriteria;

public interface SpecificationVisibilityPredicateBuilder {

	<T> Predicate buildVisibilityPredicate(CriteriaBuilder builder, Root<T> root, FilterCriteria criteria,
			String userField, String idField, String publicField);

}

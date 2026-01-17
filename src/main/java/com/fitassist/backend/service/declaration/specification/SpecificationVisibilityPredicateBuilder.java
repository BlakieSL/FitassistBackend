package com.fitassist.backend.service.declaration.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface SpecificationVisibilityPredicateBuilder {

	<T> Predicate buildVisibilityPredicate(CriteriaBuilder builder, Root<T> root, FilterCriteria criteria,
			String userField, String idField, String publicField);

}

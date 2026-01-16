package com.fitassist.backend.specification;

import org.springframework.data.jpa.domain.Specification;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;

@FunctionalInterface
public interface SpecificationFactory<T> {

	Specification<T> createSpecification(FilterCriteria criteria, SpecificationDependencies dependencies);

}

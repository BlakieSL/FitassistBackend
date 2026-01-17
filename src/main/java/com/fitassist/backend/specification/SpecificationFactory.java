package com.fitassist.backend.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import org.springframework.data.jpa.domain.Specification;

@FunctionalInterface
public interface SpecificationFactory<T> {

	Specification<T> createSpecification(FilterCriteria criteria, SpecificationDependencies dependencies);

}

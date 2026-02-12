package com.fitassist.backend.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.specification.filter.FilterDataOption;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SpecificationBuilder<T> {

	private final FilterDto filterDto;

	private final SpecificationFactory<T> specificationFactory;

	private final SpecificationDependencies dependencies;

	public SpecificationBuilder(FilterDto filterDto, SpecificationFactory<T> specificationFactory,
			SpecificationDependencies dependencies) {
		this.filterDto = filterDto;
		this.specificationFactory = specificationFactory;
		this.dependencies = dependencies;
	}

	public static <T> SpecificationBuilder<T> of(FilterDto filterDto, SpecificationFactory<T> specificationFactory,
			SpecificationDependencies dependencies) {
		return new SpecificationBuilder<>(filterDto, specificationFactory, dependencies);
	}

	public Specification<T> build() {
		List<FilterCriteria> criteriaList = filterDto.getFilterCriteria();
		if (criteriaList == null || criteriaList.isEmpty()) {
			return specificationFactory.createSpecification(new FilterCriteria(), dependencies);
		}

		Specification<T> result = specificationFactory.createSpecification(criteriaList.getFirst(), dependencies);

		for (int i = 1; i < criteriaList.size(); i++) {
			Specification<T> spec = specificationFactory.createSpecification(criteriaList.get(i), dependencies);

			if (filterDto.getDataOption() == FilterDataOption.AND) {
				result = result.and(spec);
			}
			else {
				result = result.or(spec);
			}
		}

		return result;
	}

}

package source.code.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.helper.Enum.filter.FilterDataOption;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

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
			return (root, query, builder) -> null;
		}

		Specification<T> result = specificationFactory.createSpecification(criteriaList.get(0), dependencies);

		for (int i = 1; i < criteriaList.size(); i++) {
			Specification<T> spec = specificationFactory.createSpecification(criteriaList.get(i), dependencies);

			if (filterDto.getDataOption() == FilterDataOption.AND) {
				result = result.and(spec);
			} else {
				result = result.or(spec);
			}
		}

		return result;
	}

}

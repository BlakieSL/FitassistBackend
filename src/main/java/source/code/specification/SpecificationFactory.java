package source.code.specification;

import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.service.implementation.specificationHelpers.SpecificationDependencies;

@FunctionalInterface
public interface SpecificationFactory<T> {
    Specification<T> createSpecification(FilterCriteria criteria, SpecificationDependencies dependencies);
}

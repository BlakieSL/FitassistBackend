package source.code.specification;

import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;

@FunctionalInterface
public interface SpecificationFactory<T> {
    Specification<T> createSpecification(FilterCriteria criteria);
}

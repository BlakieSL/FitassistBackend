package source.code.specification;

import org.springframework.data.jpa.domain.Specification;
import source.code.dto.POJO.FilterCriteria;
import source.code.dto.Request.filter.FilterDto;
import source.code.helper.Enum.FilterDataOption;

import java.util.List;

public class SpecificationBuilder<T> {
    private final FilterDto filterDto;
    private final SpecificationFactory<T> specificationFactory;

    public SpecificationBuilder(FilterDto filterDto, SpecificationFactory<T> specificationFactory) {
        this.filterDto = filterDto;
        this.specificationFactory = specificationFactory;
    }

    public static <T> SpecificationBuilder<T> of(FilterDto filterDto, SpecificationFactory<T> specificationFactory) {
        return new SpecificationBuilder<>(filterDto, specificationFactory);
    }

    public Specification<T> build() {
        List<FilterCriteria> criteriaList = filterDto.getFilterCriteria();
        if (criteriaList.isEmpty()) {
            return null;
        }

        Specification<T> result = specificationFactory.createSpecification(criteriaList.get(0));

        for (int i = 1; i < criteriaList.size(); i++) {
            Specification<T> spec = specificationFactory.createSpecification(criteriaList.get(i));

            result = filterDto.getDataOption() == FilterDataOption.AND
                    ? Specification.where(result).and(spec)
                    : Specification.where(result).or(spec);
        }
        return result;
    }
}

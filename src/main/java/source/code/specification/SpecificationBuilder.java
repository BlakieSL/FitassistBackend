package source.code.specification;

import org.springframework.data.jpa.domain.Specification;
import source.code.dto.Request.Filter.FilterDto;
import source.code.helper.Enum.FilterDataOption;
import source.code.pojo.FilterCriteria;
import source.code.specification.factory.SpecificationFactory;

import java.util.List;

public class SpecificationBuilder<T> {
  private final FilterDto filterDto;
  private final SpecificationFactory<T> specificationFactory;
  public SpecificationBuilder(FilterDto filterDto, SpecificationFactory<T> specificationFactory) {
    this.filterDto = filterDto;
    this.specificationFactory = specificationFactory;
  }

  public Specification<T> build() {
    List<FilterCriteria> criteriaList = filterDto.getFilterCriteria();
    if (criteriaList.isEmpty()) {
      return null;
    }

    Specification<T> result = specificationFactory.createSpecification(criteriaList.get(0));

    for (int i = 1; i < criteriaList.size(); i++) {
      Specification<T> spec = specificationFactory.createSpecification(criteriaList.get(i));

      result = filterDto.getDataOption() == FilterDataOption.ALL
              ? Specification.where(result).and(spec)
              : Specification.where(result).or(spec);
    }
    return result;
  }
}

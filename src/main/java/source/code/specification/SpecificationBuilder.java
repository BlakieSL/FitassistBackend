package source.code.specification;

import org.springframework.data.jpa.domain.Specification;
import source.code.dto.Request.Filter.FilterDto;
import source.code.helper.Enum.FilterDataOption;
import source.code.pojo.FilterCriteria;

import java.util.List;

public class SpecificationBuilder<T> {
  private final FilterDto filterDto;

  public SpecificationBuilder(FilterDto filterDto) {
    this.filterDto = filterDto;
  }

  public Specification<T> build() {
    List<FilterCriteria> criteriaList = filterDto.getFilterCriteria();
    if (criteriaList.isEmpty()) {
      return null;
    }

    Specification<T> result = new GenericSpecification<>(criteriaList.get(0));

    for (int i = 1; i < criteriaList.size(); i++) {
      Specification<T> spec = new GenericSpecification<>(criteriaList.get(i));

      result = filterDto.getDataOption() == FilterDataOption.ALL
              ? Specification.where(result).or(spec)
              : Specification.where(result).and(spec);
    }
    return result;
  }
}

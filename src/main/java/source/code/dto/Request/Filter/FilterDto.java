package source.code.dto.Request.Filter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.POJO.FilterCriteria;
import source.code.helper.Enum.FilterDataOption;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
  @NotNull
  private List<FilterCriteria> filterCriteria;
  private FilterDataOption dataOption;

  public static FilterDto createWithSingleCriteria(FilterCriteria filterCriteria) {
    return new FilterDto(List.of(filterCriteria), FilterDataOption.AND);
  }
}

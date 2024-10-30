package source.code.dto.Request.Filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.FilterDataOption;
import source.code.pojo.FilterCriteria;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
  private List<FilterCriteria> filterCriteria;
  private FilterDataOption dataOption;
}

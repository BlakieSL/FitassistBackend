package source.code.dto.POJO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.FilterOperation;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FilterCriteria {
    private String filterKey;
    private Object value;
    private FilterOperation operation;
}

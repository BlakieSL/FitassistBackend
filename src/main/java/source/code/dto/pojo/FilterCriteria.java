package source.code.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.filter.FilterOperation;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class
FilterCriteria {
    private String filterKey;
    private Object value;
    private FilterOperation operation;
    private Boolean isPublic;



    public static FilterCriteria of (String filterKey, Object value, FilterOperation operation) {
        var criteria = new FilterCriteria();
        criteria.setFilterKey(filterKey);
        criteria.setValue(value);
        criteria.setOperation(operation);
        return criteria;
    }
}

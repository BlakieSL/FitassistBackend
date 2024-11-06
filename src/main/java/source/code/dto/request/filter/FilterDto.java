package source.code.dto.request.filter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.FilterDataOption;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FilterDto {
    @NotNull
    private List<FilterCriteria> filterCriteria;
    private FilterDataOption dataOption;
}

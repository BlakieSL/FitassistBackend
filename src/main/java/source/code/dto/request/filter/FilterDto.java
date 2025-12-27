package source.code.dto.request.filter;

import jakarta.validation.constraints.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FilterCriteria;
import source.code.helper.Enum.filter.FilterDataOption;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FilterDto {

    @NotNull
    private List<FilterCriteria> filterCriteria;

    private FilterDataOption dataOption;

}

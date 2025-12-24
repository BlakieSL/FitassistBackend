package source.code.dto.response.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.FoodMacros;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SearchResponseDto implements Serializable {
    private Integer id;
    private String name;
    private String type;
    private FoodMacros foodMacros;
}

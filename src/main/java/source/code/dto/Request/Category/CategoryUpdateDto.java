package source.code.dto.Request.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDto {
    private Integer id;
    private String name;
    private String iconUrl;
    private String gradient;
}

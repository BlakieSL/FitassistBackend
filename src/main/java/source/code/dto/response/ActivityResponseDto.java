package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDto {
    private Integer id;
    private String name;
    private double met;
    private String categoryName;
    private int categoryId;
}

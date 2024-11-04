package source.code.dto.Response.Category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private static final int NAME_MAX_LENGTH = 50;

    @Size(max = NAME_MAX_LENGTH)
    private String name;

    private String iconUrl;

    private String gradient;
}

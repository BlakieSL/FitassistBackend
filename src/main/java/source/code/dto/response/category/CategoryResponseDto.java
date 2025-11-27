package source.code.dto.response.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto implements Serializable {
    private static final int NAME_MAX_LENGTH = 50;

    private int id;

    @Size(max = NAME_MAX_LENGTH)
    private String name;
}

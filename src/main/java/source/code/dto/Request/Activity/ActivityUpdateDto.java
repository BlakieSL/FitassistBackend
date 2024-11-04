package source.code.dto.Request.Activity;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityUpdateDto {
    private static final int NAME_MAX_LENGTH = 50;

    @Size(max = NAME_MAX_LENGTH)
    private String name;

    @Positive
    private Double met;

    private Integer categoryId;
}

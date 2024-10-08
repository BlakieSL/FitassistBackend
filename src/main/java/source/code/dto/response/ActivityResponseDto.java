package source.code.dto.response;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityResponseDto {
    private Integer id;
    private String name;
    private double met;
    private String categoryName;
    private int categoryId;
}

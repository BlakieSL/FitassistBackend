package source.code.dto.response.activity;

import lombok.*;
import source.code.helper.BaseUserEntity;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityResponseDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private BigDecimal met;
    private String categoryName;
    private int categoryId;
    private String imageName;
    private String firstImageUrl;
}

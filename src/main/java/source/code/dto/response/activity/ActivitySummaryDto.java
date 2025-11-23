package source.code.dto.response.activity;

import lombok.*;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySummaryDto implements BaseUserEntity, Serializable {
    private Integer id;
    private String name;
    private BigDecimal met;
    private String categoryName;
    private int categoryId;
    private String imageName;
    private String firstImageUrl;
    private LocalDateTime userActivityInteractionCreatedAt;
}

package source.code.dto.response.activity;

import lombok.*;
import source.code.helper.BaseUserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
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
    private LocalDateTime userActivityInteractionCreatedAt;

    public ActivityResponseDto(Integer id, String name, BigDecimal met, String categoryName,
                               int categoryId, String imageName, String firstImageUrl) {
        this.id = id;
        this.name = name;
        this.met = met;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.imageName = imageName;
        this.firstImageUrl = firstImageUrl;
    }

    public ActivityResponseDto(Integer id, String name, BigDecimal met, String categoryName,
                               int categoryId, String imageName, String firstImageUrl,
                               LocalDateTime userActivityInteractionCreatedAt) {
        this.id = id;
        this.name = name;
        this.met = met;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.imageName = imageName;
        this.firstImageUrl = firstImageUrl;
        this.userActivityInteractionCreatedAt = userActivityInteractionCreatedAt;
    }
}

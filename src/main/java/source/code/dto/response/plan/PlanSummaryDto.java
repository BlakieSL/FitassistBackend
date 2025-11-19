package source.code.dto.response.plan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.PlanTypeShortDto;
import source.code.helper.BaseUserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PlanSummaryDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private String authorUsername;
    private Integer authorId;
    private String authorImageUrl;
    private String imageName;
    private String firstImageUrl;
    private int likesCount;
    private int savesCount;
    private int views;
    private PlanTypeShortDto planType;
    private LocalDateTime createdAt;
    private LocalDateTime interactedWithAt;

    public PlanSummaryDto(Integer id, String name, String description, boolean isPublic,
                          String authorUsername, Integer authorId, String authorImageUrl,
                          String imageName, String firstImageUrl, int likesCount, int savesCount,
                          int views, Integer planTypeId, String planTypeName,  LocalDateTime createdAt,
                          LocalDateTime interactedWithAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.authorUsername = authorUsername;
        this.authorId = authorId;
        this.authorImageUrl = authorImageUrl;
        this.imageName = imageName;
        this.firstImageUrl = firstImageUrl;
        this.likesCount = likesCount;
        this.savesCount = savesCount;
        this.views = views;
        this.planType = new PlanTypeShortDto(planTypeId, planTypeName);
        this.createdAt = createdAt;
        this.interactedWithAt = interactedWithAt;
    }
}
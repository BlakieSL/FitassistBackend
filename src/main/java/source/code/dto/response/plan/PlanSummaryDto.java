package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.PlanTypeShortDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanSummaryDto implements BaseUserEntity, Serializable {
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String firstImageName;
    private String authorImageUrl;
    private String firstImageUrl;
    private int likesCount;
    private int savesCount;
    private int views;
    private PlanTypeShortDto planType;
    private LocalDateTime createdAt;
    private LocalDateTime interactedWithAt;
}
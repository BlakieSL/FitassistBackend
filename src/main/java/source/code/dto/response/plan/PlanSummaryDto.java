package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.model.PlanStructureType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private String firstImageName;
    private String firstImageUrl;
    private PlanStructureType planStructureType;

    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String authorImageUrl;

    private LocalDateTime interactedWithAt;

    private long likesCount;
    private long dislikesCount;
    private long savesCount;
    private long views;

    private Boolean liked;
    private Boolean disliked;
    private Boolean saved;
}
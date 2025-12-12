package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  RecipeSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private short minutesToPrepare;
    private List<CategoryResponseDto> categories = new ArrayList<>();

    private String firstImageName;
    private String firstImageUrl;

    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String authorImageUrl;

    private LocalDateTime interactedWithAt;

    private long views;
    private long likesCount;
    private long dislikesCount;
    private long savesCount;
    private long ingredientsCount;

    private Boolean liked;
    private Boolean disliked;
    private Boolean saved;
}
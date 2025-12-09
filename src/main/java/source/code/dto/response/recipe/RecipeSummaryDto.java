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
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private short minutesToPrepare;
    private long views;

    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String authorImageUrl;

    private String firstImageName;
    private String firstImageUrl;

    private long likesCount;
    private long dislikesCount;
    private long savesCount;
    private long ingredientsCount;

    private List<CategoryResponseDto> categories = new ArrayList<>();
    private LocalDateTime userRecipeInteractionCreatedAt;
}
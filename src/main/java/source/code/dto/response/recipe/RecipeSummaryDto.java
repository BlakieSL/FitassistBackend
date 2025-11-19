package source.code.dto.response.recipe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.helper.BaseUserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeSummaryDto implements BaseUserEntity {
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
    private int ingredientsCount;
    private List<RecipeCategoryShortDto> categories = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime userRecipeInteractionCreatedAt;

    public RecipeSummaryDto(Integer id, String name, String description, boolean isPublic,
                            String authorUsername, Integer authorId, String authorImageUrl,
                            String imageName, String firstImageUrl, int likesCount, int savesCount,
                            int views, int ingredientsCount, LocalDateTime createdAt,
                            LocalDateTime userRecipeInteractionCreatedAt) {
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
        this.ingredientsCount = ingredientsCount;
        this.createdAt = createdAt;
        this.userRecipeInteractionCreatedAt = userRecipeInteractionCreatedAt;
    }
}
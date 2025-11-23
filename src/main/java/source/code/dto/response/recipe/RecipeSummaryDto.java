package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSummaryDto implements BaseUserEntity, Serializable {
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
    private int ingredientsCount;
    private List<RecipeCategoryShortDto> categories = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime userRecipeInteractionCreatedAt;
}
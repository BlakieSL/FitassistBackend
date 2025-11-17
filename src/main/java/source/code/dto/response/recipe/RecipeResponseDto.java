package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.helper.BaseUserEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Integer userId;
    private String authorUsername;
    private Integer authorId;
    private String authorImageUrl;
    private String imageName;
    private String firstImageUrl;
    private List<RecipeCategoryShortDto> categories;
}

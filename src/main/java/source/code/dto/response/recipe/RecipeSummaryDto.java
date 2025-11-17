package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSummaryDto implements source.code.helper.BaseUserEntity {
    private Integer id;
    private String name;
    private boolean isPublic;
    private String authorUsername;
    private Integer authorId;
    private String authorImageUrl;
    private String imageName;
    private String firstImageUrl;
    private int likesCount;
    private int savesCount;
}

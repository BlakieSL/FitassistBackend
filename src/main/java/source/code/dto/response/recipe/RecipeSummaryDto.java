package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSummaryDto {
    private int id;
    private String name;
    private boolean isPublic;
    private String authorUsername;
    private int likesCount;
    private int savesCount;
}

package source.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Integer id;
    private String name;
    private String description;
    private String text;
    private Double score;
    List<RecipeCategoryShortDto> categories;
}

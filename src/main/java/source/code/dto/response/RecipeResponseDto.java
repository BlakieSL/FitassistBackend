package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.other.RecipeCategoryShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto {
    private Integer id;
    private String name;
    private String description;
    private String text;
    private Double score;
    List<RecipeCategoryShortDto> categories;
}

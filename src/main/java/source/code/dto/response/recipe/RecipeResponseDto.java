package source.code.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.RecipeCategoryShortDto;
import source.code.dto.pojo.RecipeFoodDto;
import source.code.dto.response.text.RecipeInstructionResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private short minutesToPrepare;
    private long views;

    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String authorImageUrl;

    private long likesCount;
    private long dislikesCount;
    private long savesCount;

    private boolean liked;
    private boolean disliked;
    private boolean saved;

    private BigDecimal totalCalories;

    private List<RecipeFoodDto> foods;
    private List<RecipeInstructionResponseDto> instructions;
    private List<RecipeCategoryShortDto> categories;
    private List<String> imageUrls;

}

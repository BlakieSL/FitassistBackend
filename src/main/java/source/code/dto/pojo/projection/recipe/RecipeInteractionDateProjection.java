package source.code.dto.pojo.projection.recipe;

import java.time.LocalDateTime;

public interface RecipeInteractionDateProjection {
    Integer getRecipeId();
    LocalDateTime getCreatedAt();
}

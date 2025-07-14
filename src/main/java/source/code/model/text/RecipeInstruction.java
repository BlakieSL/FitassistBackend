package source.code.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.recipe.Recipe;

@Entity
@DiscriminatorValue("RECIPE_INSTRUCTION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeInstruction extends TextBase {
    private String title;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public static RecipeInstruction of(short number, String title, String text, Recipe recipe) {
        RecipeInstruction instruction = new RecipeInstruction();
        instruction.setOrderIndex(number);
        instruction.setTitle(title);
        instruction.setText(text);
        instruction.setRecipe(recipe);
        return instruction;
    }

    public static RecipeInstruction of(Integer id, Recipe recipe) {
        RecipeInstruction instruction = new RecipeInstruction();
        instruction.setId(id);
        instruction.setRecipe(recipe);
        return instruction;
    }
}

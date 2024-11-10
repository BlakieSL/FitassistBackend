package source.code.model.text;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    public static RecipeInstruction createWithIdAndRecipe(int id, Recipe recipe) {
        RecipeInstruction instruction = new RecipeInstruction();
        instruction.setId(id);
        instruction.setRecipe(recipe);
        return instruction;
    }

    public static RecipeInstruction createWithNumberTitleText(short number, String title, String text) {
        RecipeInstruction instruction = new RecipeInstruction();
        instruction.setNumber(number);
        instruction.setTitle(title);
        instruction.setText(text);
        return instruction;
    }
}

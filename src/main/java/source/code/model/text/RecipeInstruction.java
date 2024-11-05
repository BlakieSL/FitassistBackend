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
@DiscriminatorValue("RECIPE_INSTRUCTIONS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeInstruction extends TextBase {

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
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

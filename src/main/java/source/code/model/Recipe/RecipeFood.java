package source.code.model.Recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Food.Food;

@Entity
@Table(name = "recipe_food")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeFood {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @Positive
  @Column(nullable = false)
  private int amount;

  @ManyToOne
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  @ManyToOne
  @JoinColumn(name = "food_id", nullable = false)
  private Food food;

  public static RecipeFood createWithAmountRecipeFood(
          int amount, Recipe recipe, Food food) {

    RecipeFood recipeFood = new RecipeFood();
    recipeFood.setAmount(amount);
    recipeFood.setRecipe(recipe);
    recipeFood.setFood(food);

    return recipeFood;
  }
}

package source.code.model.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import lombok.*;
import source.code.model.food.Food;

@Entity
@Table(name = "recipe_food")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeFood {

	public static final String FOOD = "food";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Positive
	@Column(nullable = false)
	private BigDecimal quantity;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "recipe_id", nullable = false)
	private Recipe recipe;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "food_id", nullable = false)
	private Food food;

	public static RecipeFood of(BigDecimal quantity, Recipe recipe, Food food) {

		RecipeFood recipeFood = new RecipeFood();
		recipeFood.setQuantity(quantity);
		recipeFood.setRecipe(recipe);
		recipeFood.setFood(food);

		return recipeFood;
	}

}

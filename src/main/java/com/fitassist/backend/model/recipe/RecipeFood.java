package com.fitassist.backend.model.recipe;

import com.fitassist.backend.model.food.Food;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
	@Column(nullable = false, precision = 7, scale = 2)
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

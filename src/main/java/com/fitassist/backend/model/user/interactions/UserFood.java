package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_food")
@NamedEntityGraph(name = UserFood.GRAPH_WITH_FOOD_DETAILS,
		attributeNodes = { @NamedAttributeNode(value = "food", subgraph = "food-subgraph") },
		subgraphs = { @NamedSubgraph(name = "food-subgraph",
				attributeNodes = { @NamedAttributeNode("foodCategory"), @NamedAttributeNode("mediaList") }) })
@Getter
@Setter
public class UserFood extends UserInteractionBase {

	public static final String GRAPH_WITH_FOOD_DETAILS = "UserFood.withFoodDetails";

	@NotNull
	@ManyToOne
	@JoinColumn(name = "food_id", nullable = false)
	private Food food;

	public static UserFood of(User user, Food food) {
		UserFood userFood = new UserFood();
		userFood.setUser(user);
		userFood.setFood(food);
		return userFood;
	}

}

package com.fitassist.backend.model.user;

import com.fitassist.backend.model.food.Food;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_food")
@NamedEntityGraph(name = "UserFood.withFoodDetails",
		attributeNodes = { @NamedAttributeNode(value = "food", subgraph = "food-subgraph") },
		subgraphs = { @NamedSubgraph(name = "food-subgraph",
				attributeNodes = { @NamedAttributeNode("foodCategory"), @NamedAttributeNode("mediaList") }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFood {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "food_id", nullable = false)
	private Food food;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public static UserFood of(User user, Food food) {
		UserFood userFood = new UserFood();
		userFood.setUser(user);
		userFood.setFood(food);

		return userFood;
	}

}

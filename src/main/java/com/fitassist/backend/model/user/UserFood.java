package com.fitassist.backend.model.user;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.fitassist.backend.model.food.Food;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_food")
@NamedEntityGraph(name = UserFood.GRAPH_WITH_FOOD_DETAILS,
		attributeNodes = { @NamedAttributeNode(value = "food", subgraph = "food-subgraph") },
		subgraphs = { @NamedSubgraph(name = "food-subgraph",
				attributeNodes = { @NamedAttributeNode("foodCategory"), @NamedAttributeNode("mediaList") }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFood {

	public static final String GRAPH_WITH_FOOD_DETAILS = "UserFood.withFoodDetails";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
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

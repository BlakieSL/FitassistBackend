package com.fitassist.backend.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.model.recipe.Recipe;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "recipie_id", nullable = false)
	private Recipe recipe;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TypeOfInteraction type;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public static UserRecipe createWithUserRecipeType(User user, Recipe recipe, TypeOfInteraction type) {

		UserRecipe userRecipe = new UserRecipe();
		userRecipe.setUser(user);
		userRecipe.setRecipe(recipe);
		userRecipe.setType(type);

		return userRecipe;
	}

}

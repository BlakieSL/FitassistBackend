package com.fitassist.backend.model.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a category used to classify recipes based on when or how they are typically
 * consumed.
 *
 * <p>
 * <strong>Known predefined values:</strong>
 *
 * <ul>
 * <li>{@code BREAKFAST}
 * <li>{@code LUNCH}
 * <li>{@code DINNER}
 * <li>{@code SNACK}
 * <li>{@code DESSERT}
 * <li>{@code APPETIZER}
 * <li>{@code SIDE_DISH}
 * <li>{@code MAIN_COURSE}
 * </ul>
 *
 * <p>
 * Note: These values are not hardcoded and may be extended or modified through the
 * application.
 */
@Entity
@Table(name = "recipe_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCategory {

	private static final int NAME_MAX_LENGTH = 50;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@OneToMany(mappedBy = "recipeCategory")
	private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new HashSet<>();

}

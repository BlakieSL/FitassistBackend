package com.fitassist.backend.model.recipe;

import com.fitassist.backend.model.IndexedEntity;
import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.text.RecipeInstruction;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserRecipe;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "recipe")
@NamedEntityGraph(name = "Recipe.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Recipe.summary",
		attributeNodes = { @NamedAttributeNode("user"), @NamedAttributeNode("mediaList"),
				@NamedAttributeNode(value = "recipeCategoryAssociations", subgraph = "rca-subgraph") },
		subgraphs = { @NamedSubgraph(name = "rca-subgraph", attributeNodes = @NamedAttributeNode("recipeCategory")) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe implements IndexedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	@Column(nullable = false, length = TEXT_MAX_LENGTH)
	private String description;

	@NotNull
	@Max(1440)
	@Column(name = "minutes_to_prepare", nullable = false)
	private Short minutesToPrepare;

	@NotNull
	@Column(nullable = false, name = "is_public")
	private Boolean isPublic = false;

	@Column(nullable = false)
	private long views = 0L;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "recipe", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<RecipeInstruction> recipeInstructions = new LinkedHashSet<>();

	@OneToMany(mappedBy = "recipe", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	@OrderBy("id ASC")
	private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new LinkedHashSet<>();

	@OneToMany(mappedBy = "recipe", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	@OrderBy("id ASC")
	private final Set<RecipeFood> recipeFoods = new LinkedHashSet<>();

	@OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE)
	private final Set<UserRecipe> userRecipes = new HashSet<>();

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'RECIPE'")
	private List<Media> mediaList = new ArrayList<>();

	@Override
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public static Recipe of(Integer id, User user) {
		Recipe recipe = new Recipe();
		recipe.setId(id);
		recipe.setUser(user);
		return recipe;
	}

	public static Recipe of(User user) {
		Recipe recipe = new Recipe();
		recipe.setUser(user);
		return recipe;
	}

}

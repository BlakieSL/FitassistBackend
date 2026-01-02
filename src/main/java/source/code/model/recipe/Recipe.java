package source.code.model.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.helper.IndexedEntity;
import source.code.model.media.Media;
import source.code.model.text.RecipeInstruction;
import source.code.model.user.User;
import source.code.model.user.UserRecipe;

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

	private static final int NAME_MAX_LENGTH = 100;

	private static final int DESCRIPTION_MAX_LENGTH = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = DESCRIPTION_MAX_LENGTH)
	@Column(nullable = false)
	private String description;

	@NotNull
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

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "recipe", cascade = { CascadeType.PERSIST }, orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<RecipeInstruction> recipeInstructions = new LinkedHashSet<>();

	@OneToMany(mappedBy = "recipe", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	@OrderBy("id ASC")
	private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new LinkedHashSet<>();

	@OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
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

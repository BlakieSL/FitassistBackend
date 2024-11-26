package source.code.model.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.search.IndexedEntity;
import source.code.model.text.RecipeInstruction;
import source.code.model.user.profile.User;
import source.code.model.user.UserRecipe;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipe")
@NamedEntityGraph(name = "Recipe.withoutAssociations", attributeNodes = {})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe implements IndexedEntity {
    private static final int NAME_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MAX_LENGTH = 255;
    private static final int TEXT_MAX_LENGTH = 2000;

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

    @NotBlank
    @Size(max = TEXT_MAX_LENGTH)
    @Column(nullable = false, length = TEXT_MAX_LENGTH)
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "recipe",
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private final Set<RecipeInstruction> recipeInstructions = new HashSet<>();

    @OneToMany(mappedBy = "recipe",
            cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<RecipeFood> recipeFoods = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE)
    private final Set<UserRecipe> userRecipes = new HashSet<>();

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

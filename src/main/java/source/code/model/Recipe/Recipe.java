package source.code.model.Recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.User.UserRecipe;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
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
  @Column(nullable = false)
  private Double score;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new HashSet<>();
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE)
  private final Set<UserRecipe> userRecipes = new HashSet<>();
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<RecipeFood> recipeFoods = new HashSet<>();
}

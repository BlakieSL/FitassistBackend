package source.code.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Recipe.Recipe;

@Entity
@Table(name = "user_recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecipe implements BaseUserEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "recipie_id", nullable = false)
  private Recipe recipe;

  @NotNull
  @Column(nullable = false)
  private short type;

  public static UserRecipe createWithUserRecipeType(
          User user, Recipe recipe, short type) {

    UserRecipe userRecipe = new UserRecipe();
    userRecipe.setUser(user);
    userRecipe.setRecipe(recipe);
    userRecipe.setType(type);

    return userRecipe;
  }
}

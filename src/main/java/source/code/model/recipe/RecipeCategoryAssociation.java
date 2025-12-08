package source.code.model.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "recipe_category_association")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecipeCategoryAssociation {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipe_category_id", nullable = false)
    private RecipeCategory recipeCategory;

    public static RecipeCategoryAssociation createWithRecipeCategory(RecipeCategory recipeCategory) {
        RecipeCategoryAssociation recipeCategoryAssociation = new RecipeCategoryAssociation();
        recipeCategoryAssociation.setRecipeCategory(recipeCategory);

        return recipeCategoryAssociation;
    }
}

package source.code.model.recipe;

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
 * Represents a category used to classify recipes based on when or how they are typically consumed.
 *
 * <p><strong>Known predefined values:</strong></p>
 * <ul>
 *     <li>{@code BREAKFAST}</li>
 *     <li>{@code LUNCH}</li>
 *     <li>{@code DINNER}</li>
 *     <li>{@code SNACK}</li>
 *     <li>{@code DESSERT}</li>
 *     <li>{@code APPETIZER}</li>
 *     <li>{@code SIDE_DISH}</li>
 *     <li>{@code MAIN_COURSE}</li>
 * </ul>
 *
 * <p>Note: These values are not hardcoded and may be extended or modified through the application.</p>
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

    @OneToMany(mappedBy = "recipeCategory", cascade = CascadeType.ALL)
    private final Set<RecipeCategoryAssociation> recipeCategoryAssociations = new HashSet<>();
}

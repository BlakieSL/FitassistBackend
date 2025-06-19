package source.code.model.food;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.search.IndexedEntity;
import source.code.model.daily.DailyFoodItem;
import source.code.model.recipe.RecipeFood;
import source.code.model.user.UserFood;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "food")
@NamedEntityGraph(name = "Food.withoutAssociations", attributeNodes = {})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Food implements IndexedEntity {
    private static final int MAX_NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double calories;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private double protein;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private double fat;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private double carbohydrates;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "food_category_id", nullable = false)
    private FoodCategory foodCategory;

    @OneToMany(mappedBy = "food", cascade = CascadeType.REMOVE)
    private final Set<DailyFoodItem> dailyFoodItems = new HashSet<>();

    @OneToMany(mappedBy = "food", cascade = CascadeType.REMOVE)
    private final Set<RecipeFood> recipeFoods = new HashSet<>();

    @OneToMany(mappedBy = "food", cascade = CascadeType.REMOVE)
    private final Set<UserFood> userFoods = new HashSet<>();

    public static Food createWithId(int id) {
        Food food = new Food();
        food.setId(id);
        return food;
    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}

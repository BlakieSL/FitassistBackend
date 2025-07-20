package source.code.model.food;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.search.IndexedEntity;
import source.code.model.daily.DailyCartFood;
import source.code.model.recipe.RecipeFood;
import source.code.model.user.UserFood;

import java.math.BigDecimal;
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
    private BigDecimal calories;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal protein;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal fat;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal carbohydrates;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "food_category_id", nullable = false)
    private FoodCategory foodCategory;

    @OneToMany(mappedBy = "food", cascade = CascadeType.REMOVE)
    private final Set<DailyCartFood> dailyCartFoods = new HashSet<>();

    @OneToMany(mappedBy = "food")
    private final Set<RecipeFood> recipeFoods = new HashSet<>();

    @OneToMany(mappedBy = "food", cascade = CascadeType.REMOVE)
    private final Set<UserFood> userFoods = new HashSet<>();

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}

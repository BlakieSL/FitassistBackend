package source.code.model.food;

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
 * Represents a category used to classify different types of food.
 *
 * <p>
 * Each food category can have associated foods and has metadata like an icon and
 * background gradient for UI representation.
 *
 * <p>
 * <strong>Known predefined values:</strong>
 *
 * <ul>
 * <li>{@code FRUIT}
 * <li>{@code VEGETABLE}
 * <li>{@code GRAIN}
 * <li>{@code MEAT}
 * <li>{@code FISH}
 * <li>{@code DAIRY}
 * <li>{@code SEED}
 * <li>{@code SPICE}
 * <li>{@code OIL}
 * <li>{@code BEVERAGE}
 * </ul>
 *
 * <p>
 * Note: These values are not hardcoded and may be extended or modified through the
 * application.
 */
@Entity
@Table(name = "food_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCategory {

	private static final int MAX_NAME_LENGTH = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = MAX_NAME_LENGTH)
	private String name;

	@OneToMany(mappedBy = "foodCategory")
	private final Set<Food> foods = new HashSet<>();

}

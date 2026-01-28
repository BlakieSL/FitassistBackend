package com.fitassist.backend.model.food;

import com.fitassist.backend.model.IndexedEntity;
import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import com.fitassist.backend.model.daily.DailyCartFood;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.recipe.RecipeFood;
import com.fitassist.backend.model.user.UserFood;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "food")
@NamedEntityGraph(name = "Food.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Food.summary",
		attributeNodes = { @NamedAttributeNode("foodCategory"), @NamedAttributeNode("mediaList") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Food implements IndexedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 1)
	@Max(900)
	@Column(nullable = false, precision = 4, scale = 1)
	private BigDecimal calories;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal protein;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal fat;

	@NotNull
	@PositiveOrZero
	@Digits(integer = 3, fraction = 2)
	@Max(100)
	@Column(nullable = false, precision = 5, scale = 2)
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

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'FOOD'")
	private List<Media> mediaList = new ArrayList<>();

	@Override
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

}

package com.fitassist.backend.model.food;

import com.fitassist.backend.model.IndexedEntity;
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

	private static final int MAX_NAME_LENGTH = 500;

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

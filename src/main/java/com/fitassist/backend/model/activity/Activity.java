package com.fitassist.backend.model.activity;

import com.fitassist.backend.model.IndexedEntity;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.user.interactions.UserActivity;
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

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;

@Entity
@Table(name = "activity")
@NamedEntityGraph(name = Activity.GRAPH_BASE, attributeNodes = {})
@NamedEntityGraph(name = Activity.GRAPH_DETAIL,
		attributeNodes = { @NamedAttributeNode("dailyCartActivities"), @NamedAttributeNode("userActivities") })
@NamedEntityGraph(name = Activity.GRAPH_SUMMARY,
		attributeNodes = { @NamedAttributeNode("activityCategory"), @NamedAttributeNode("mediaList") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Activity implements IndexedEntity {

	public static final String GRAPH_BASE = "Activity.base";

	public static final String GRAPH_SUMMARY = "Activity.summary";

	public static final String GRAPH_DETAIL = "Activity.detail";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@Min(1)
	@Max(25)
	@Digits(integer = 2, fraction = 1)
	@Column(nullable = false, precision = 3, scale = 1)
	private BigDecimal met;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "activity_category_id", nullable = false)
	private ActivityCategory activityCategory;

	@OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
	private final Set<DailyCartActivity> dailyCartActivities = new HashSet<>();

	@OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
	private final Set<UserActivity> userActivities = new HashSet<>();

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'ACTIVITY'")
	private List<Media> mediaList = new ArrayList<>();

	public static Activity of(int id, String name, BigDecimal met) {
		Activity activity = new Activity();
		activity.setId(id);
		activity.setName(name);
		activity.setMet(met);
		return activity;
	}

	public static Activity of(Integer id, String name) {
		return of(id, name, BigDecimal.ZERO);
	}

}

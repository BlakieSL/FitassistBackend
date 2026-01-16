package com.fitassist.backend.model.activity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import com.fitassist.backend.model.IndexedEntity;
import com.fitassist.backend.model.daily.DailyCartActivity;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.user.UserActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "activity")
@NamedEntityGraph(name = "Activity.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Activity.withAssociations",
		attributeNodes = { @NamedAttributeNode("dailyCartActivities"), @NamedAttributeNode("userActivities") })
@NamedEntityGraph(name = "Activity.summary",
		attributeNodes = { @NamedAttributeNode("activityCategory"), @NamedAttributeNode("mediaList") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Activity implements IndexedEntity {

	private static final int NAME_MAX_LENGTH = 500;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@Positive
	@Column(nullable = false)
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

	@Override
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

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

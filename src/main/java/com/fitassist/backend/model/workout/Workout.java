package com.fitassist.backend.model.workout;

import com.fitassist.backend.model.plan.Plan;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;

@Entity
@Table(name = "workout")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Workout {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotNull
	@PositiveOrZero
	@Max(480)
	@Column(nullable = false)
	private Short duration;

	@NotNull
	@Positive
	@Max(365)
	@Column(name = "order_index", nullable = false)
	private Short orderIndex;

	@NotNull
	@PositiveOrZero
	@Max(7)
	@Column(name = "rest_days_after", nullable = false)
	private Byte restDaysAfter;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@OneToMany(mappedBy = "workout", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<WorkoutSet> workoutSets = new LinkedHashSet<>();

	public static Workout of(Integer id, Plan plan) {
		Workout workout = new Workout();
		workout.setId(id);
		workout.setPlan(plan);
		return workout;
	}

	public static Workout of(Plan plan) {
		Workout workout = new Workout();
		workout.setPlan(plan);
		return workout;
	}

}

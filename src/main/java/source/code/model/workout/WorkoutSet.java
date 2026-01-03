package source.code.model.workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workout_set")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(name = "order_index", nullable = false)
	private Short orderIndex;

	@NotNull
	@Column(name = "rest_seconds", nullable = false)
	private Short restSeconds;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "workout_id", nullable = false)
	private Workout workout;

	@OneToMany(mappedBy = "workoutSet", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private Set<WorkoutSetExercise> workoutSetExercises = new LinkedHashSet<>();

	public static WorkoutSet of(Integer id, Workout workout) {
		WorkoutSet workoutSet = new WorkoutSet();
		workoutSet.setId(id);
		workoutSet.setWorkout(workout);
		return workoutSet;
	}

	public static WorkoutSet of(Workout workout) {
		WorkoutSet workoutSet = new WorkoutSet();
		workoutSet.setWorkout(workout);
		return workoutSet;
	}

}

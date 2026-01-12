package source.code.model.workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.exercise.Exercise;

import java.math.BigDecimal;

@Entity
@Table(name = "workout_set_exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetExercise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(nullable = false)
	private BigDecimal weight;

	@NotNull
	@Column(nullable = false)
	private Short repetitions;

	@NotNull
	@Column(name = "order_index", nullable = false)
	private Short orderIndex;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "workout_set_id", nullable = false)
	private WorkoutSet workoutSet;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;

	public static WorkoutSetExercise of(Integer id, WorkoutSet workoutSet) {
		WorkoutSetExercise workoutSetExercise = new WorkoutSetExercise();
		workoutSetExercise.setId(id);
		workoutSetExercise.setWorkoutSet(workoutSet);
		return workoutSetExercise;
	}

}

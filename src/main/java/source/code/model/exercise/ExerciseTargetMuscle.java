package source.code.model.exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_target_muscle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ExerciseTargetMuscle {

	public static final String TARGET_MUSCLE = "targetMuscle";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "target_muscle_id", nullable = false)
	private TargetMuscle targetMuscle;

	@NotNull
	@Column(nullable = false)
	private BigDecimal priority;

	public static ExerciseTargetMuscle createWithTargetMuscleExercise(TargetMuscle targetMuscle, Exercise exercise) {
		ExerciseTargetMuscle exerciseTargetMuscle = new ExerciseTargetMuscle();
		exerciseTargetMuscle.setTargetMuscle(targetMuscle);
		exerciseTargetMuscle.setExercise(exercise);
		exerciseTargetMuscle.setPriority(BigDecimal.valueOf(1));

		return exerciseTargetMuscle;
	}

}

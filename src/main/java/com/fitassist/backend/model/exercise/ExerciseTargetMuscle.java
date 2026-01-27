package com.fitassist.backend.model.exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "exercise_target_muscle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ExerciseTargetMuscle {

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
	@Column(nullable = false, precision = 3, scale = 2)
	private BigDecimal priority;

	public static ExerciseTargetMuscle createWithTargetMuscleExercise(TargetMuscle targetMuscle, Exercise exercise) {
		ExerciseTargetMuscle exerciseTargetMuscle = new ExerciseTargetMuscle();
		exerciseTargetMuscle.setTargetMuscle(targetMuscle);
		exerciseTargetMuscle.setExercise(exercise);
		exerciseTargetMuscle.setPriority(BigDecimal.valueOf(1));

		return exerciseTargetMuscle;
	}

}

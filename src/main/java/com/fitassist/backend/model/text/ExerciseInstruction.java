package com.fitassist.backend.model.text;

import com.fitassist.backend.model.exercise.Exercise;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("EXERCISE_INSTRUCTION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseInstruction extends TextBase {

	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	public static ExerciseInstruction of(short number, String title, String text, Exercise exercise) {
		ExerciseInstruction instruction = new ExerciseInstruction();
		instruction.setOrderIndex(number);
		instruction.setText(text);
		instruction.setTitle(title);
		instruction.setExercise(exercise);
		return instruction;
	}

}

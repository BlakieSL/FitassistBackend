package com.fitassist.backend.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.model.exercise.Exercise;

@Entity
@DiscriminatorValue("EXERCISE_TIP")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTip extends TextBase {

	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	public static ExerciseTip of(short number, String title, String text, Exercise exercise) {
		ExerciseTip tip = new ExerciseTip();
		tip.setOrderIndex(number);
		tip.setText(text);
		tip.setTitle(title);
		tip.setExercise(exercise);
		return tip;
	}

}

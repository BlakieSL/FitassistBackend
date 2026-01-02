package source.code.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import source.code.model.exercise.Exercise;

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

	public static ExerciseTip createWithNumberAndText(short number, String text) {
		ExerciseTip tip = new ExerciseTip();
		tip.setOrderIndex(number);
		tip.setText(text);
		return tip;
	}

}

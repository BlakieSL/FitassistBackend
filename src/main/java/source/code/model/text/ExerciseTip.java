package source.code.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    public static ExerciseTip createWithIdAndExercise(int id, Exercise exercise) {
        ExerciseTip tip = new ExerciseTip();
        tip.setId(id);
        tip.setExercise(exercise);
        return tip;
    }

    public static ExerciseTip createWithNumberAndText(short number, String text) {
        ExerciseTip tip = new ExerciseTip();
        tip.setNumber(number);
        tip.setText(text);
        return tip;
    }
}

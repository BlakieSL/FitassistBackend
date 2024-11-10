package source.code.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.exercise.Exercise;

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

    public static ExerciseInstruction of(short number, String text) {
        ExerciseInstruction instruction = new ExerciseInstruction();
        instruction.setNumber(number);
        instruction.setText(text);
        return instruction;
    }
}

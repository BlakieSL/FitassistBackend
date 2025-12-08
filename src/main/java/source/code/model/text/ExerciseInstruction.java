package source.code.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import source.code.model.exercise.Exercise;

@Entity
@DiscriminatorValue("EXERCISE_INSTRUCTION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExerciseInstruction extends TextBase {
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    public static ExerciseInstruction of(short number, String text) {
        ExerciseInstruction instruction = new ExerciseInstruction();
        instruction.setOrderIndex(number);
        instruction.setText(text);
        return instruction;
    }
}

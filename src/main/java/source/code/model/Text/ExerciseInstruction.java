package source.code.model.Text;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Exercise.Exercise;

@Entity
@DiscriminatorValue("EXERCISE_INSTRUCTION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseInstruction extends TextBase {
  @NotNull
  @ManyToOne
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  public static ExerciseInstruction createWithIdAndExercise(int id, Exercise exercise) {
    ExerciseInstruction instruction = new ExerciseInstruction();
    instruction.setId(id);
    instruction.setExercise(exercise);
    return instruction;
  }

  public static ExerciseInstruction createWithNumberAndText(short number, String text) {
    ExerciseInstruction instruction = new ExerciseInstruction();
    instruction.setNumber(number);
    instruction.setText(text);
    return  instruction;
  }
}

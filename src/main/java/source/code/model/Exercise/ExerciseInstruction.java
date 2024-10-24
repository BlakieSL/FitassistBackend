package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_instruction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseInstruction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @Column(nullable = false)
  private short number;

  @NotBlank
  @Column(nullable = false)
  private String text;

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

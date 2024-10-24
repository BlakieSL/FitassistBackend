package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_tip")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTip {
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

package source.code.model.Workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Exercise.Exercise;

@Entity
@Table(name = "workout_set")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @Column(nullable = false)
  private double weight;

  @NotNull
  @Column(nullable = false)
  private int repetitions;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "workout_type_id", nullable = false)
  private WorkoutType workoutType;

  @OneToOne
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;
}

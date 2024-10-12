package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercise_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "exerciseType", cascade = CascadeType.REMOVE)
  private final Set<Exercise> exercises = new HashSet<>();
}
//strength, hypertrophy, cardio, stretching, flexibility
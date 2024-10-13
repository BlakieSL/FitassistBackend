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
@Table(name = "exercise_equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseEquipment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "exerciseEquipment", cascade = CascadeType.REMOVE)
  private final Set<Exercise> exercises = new HashSet<>();

  public static ExerciseEquipment createWithId(int id) {
    ExerciseEquipment exerciseEquipment = new ExerciseEquipment();
    exerciseEquipment.setId(id);
    return exerciseEquipment;
  }
}
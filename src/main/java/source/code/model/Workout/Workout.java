package source.code.model.Workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Plan.Plan;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
  private static final int NAME_MAX_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  @Column(nullable = false, length = NAME_MAX_LENGTH)
  private String name;

  @NotNull
  @PositiveOrZero
  @Column(nullable = false)
  private int time;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @OneToMany(mappedBy = "workout",
          cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
  private final Set<WorkoutSet> workoutSets = new HashSet<>();
}

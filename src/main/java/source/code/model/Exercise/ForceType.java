package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Exercise.Exercise;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "force_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForceType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "forceType", cascade = CascadeType.REMOVE)
  private final Set<Exercise> exercises = new HashSet<>();
}

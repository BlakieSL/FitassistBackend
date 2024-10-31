package source.code.model.Other;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Exercise.Exercise;
import source.code.model.Plan.Plan;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "expertise_level")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpertiseLevel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "expertiseLevel", cascade = CascadeType.REMOVE)
  private final Set<Exercise> exercises = new HashSet<>();

  @OneToMany(mappedBy = "expertiseLevel", cascade = CascadeType.REMOVE)
  private final Set<Plan> plans = new HashSet<>();

  public static ExpertiseLevel createWithId(int id){
    ExpertiseLevel expertiseLevel = new ExpertiseLevel();
    expertiseLevel.setId(id);
    return expertiseLevel;
  }
}

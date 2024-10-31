package source.code.model.Other;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Plan.Plan;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan_equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanEquipment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "planEquipment", cascade = CascadeType.REMOVE)
  private final Set<Plan> plans = new HashSet<>();
}

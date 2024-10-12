package source.code.model.Plan;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan_category_association")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanCategoryAssociation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @ManyToOne
  @JoinColumn(name = "plan_category_id", nullable = false)
  private PlanCategory planCategory;
}

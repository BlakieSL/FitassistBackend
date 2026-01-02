package source.code.model.plan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

	public static final String PLAN_CATEGORY = "planCategory";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "plan_category_id", nullable = false)
	private PlanCategory planCategory;

	public static PlanCategoryAssociation createWithPlanAndCategory(Plan plan, PlanCategory category) {
		PlanCategoryAssociation association = new PlanCategoryAssociation();
		association.setPlanCategory(category);
		association.setPlan(plan);
		return association;
	}

}

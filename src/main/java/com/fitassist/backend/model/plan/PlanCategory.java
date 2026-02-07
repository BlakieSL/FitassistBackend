package com.fitassist.backend.model.plan;

import com.fitassist.backend.model.CategoryBase;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan_category")
@Getter
@Setter
public class PlanCategory extends CategoryBase {

	@OneToMany(mappedBy = "planCategory")
	private final Set<PlanCategoryAssociation> planCategoryAssociations = new HashSet<>();

}

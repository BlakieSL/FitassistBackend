package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_plan")
@Getter
@Setter
public class UserPlan extends UserInteractionWithType {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	public static UserPlan of(User user, Plan plan, TypeOfInteraction type) {
		UserPlan userPlan = new UserPlan();
		userPlan.setUser(user);
		userPlan.setPlan(plan);
		userPlan.setType(type);
		return userPlan;
	}

}

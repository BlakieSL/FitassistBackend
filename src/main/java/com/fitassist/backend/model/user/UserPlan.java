package com.fitassist.backend.model.user;

import com.fitassist.backend.model.plan.Plan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TypeOfInteraction type;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public static UserPlan of(User user, Plan plan, TypeOfInteraction type) {
		UserPlan userPlan = new UserPlan();
		userPlan.setUser(user);
		userPlan.setPlan(plan);
		userPlan.setType(type);
		return userPlan;
	}

}

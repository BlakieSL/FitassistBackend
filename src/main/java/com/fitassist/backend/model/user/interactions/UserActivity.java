package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_activity")
@Getter
@Setter
public class UserActivity extends UserInteractionBase {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "activity_id", nullable = false)
	private Activity activity;

	public static UserActivity of(User user, Activity activity) {
		UserActivity userActivity = new UserActivity();
		userActivity.setUser(user);
		userActivity.setActivity(activity);
		return userActivity;
	}

}

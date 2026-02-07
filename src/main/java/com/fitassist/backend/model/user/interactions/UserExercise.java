package com.fitassist.backend.model.user.interactions;

import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_exercise")
@Getter
@Setter
public class UserExercise extends UserInteractionBase {

	@NotNull
	@ManyToOne
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;

	public static UserExercise of(User user, Exercise exercise) {
		UserExercise userExercise = new UserExercise();
		userExercise.setUser(user);
		userExercise.setExercise(exercise);
		return userExercise;
	}

}

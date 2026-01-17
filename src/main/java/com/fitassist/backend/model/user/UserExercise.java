package com.fitassist.backend.model.user;

import com.fitassist.backend.model.exercise.Exercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExercise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public static UserExercise of(User user, Exercise exercise) {
		UserExercise userExercise = new UserExercise();
		userExercise.setUser(user);
		userExercise.setExercise(exercise);

		return userExercise;
	}

}

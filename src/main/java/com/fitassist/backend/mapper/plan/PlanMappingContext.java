package com.fitassist.backend.mapper.plan;

import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.plan.PlanCategory;
import com.fitassist.backend.model.user.User;
import lombok.Getter;

import java.util.List;

@Getter
public class PlanMappingContext {

	private final User user;

	private final List<PlanCategory> categories;

	private final List<Exercise> exercises;

	private PlanMappingContext(User user, List<PlanCategory> categories, List<Exercise> exercises) {
		this.user = user;
		this.categories = categories;
		this.exercises = exercises;
	}

	public static PlanMappingContext forCreate(User user, List<PlanCategory> categories, List<Exercise> exercises) {
		return new PlanMappingContext(user, categories, exercises);
	}

	public static PlanMappingContext forUpdate(List<PlanCategory> categories, List<Exercise> exercises) {
		return new PlanMappingContext(null, categories, exercises);
	}

	public Exercise getExercise(int id) {
		return exercises.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

}
